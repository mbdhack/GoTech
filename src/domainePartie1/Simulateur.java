package domainePartie1;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import javax.swing.event.MouseInputListener;



public class Simulateur implements MouseInputListener {

	private static Carte m_carte = new Carte();
	private static Vehicule m_vehicule = Vehicule.getInstance();
	private EtatDEdition m_etat = new EtatDEdition(this);
	private static ZoomModel m_zoom = new ZoomModel();
	private static Echelle m_echelle = new Echelle();
	private static Grille m_grille = new Grille(m_echelle, m_zoom);
	private Parametres m_parametres = new Parametres();
	
	private static Resultats m_resultat = new Resultats();
	private static Urgence m_urgence = Urgence.getInstance();
	private static StrategieGestion m_strategie = new StrategieGestion();

	private static StrategieAnciennete unestrategie = new StrategieAnciennete();

	private EtatSimulateur m_etatsimu = new EtatSimulateur();



	public Simulateur() {
		

	}

	public void setEtatAjouterNoeud() {
		m_etat = new EtatAjouterNoeud(this);
	}

	public void setEtatAjouterArc() {
		m_etat = new EtatAjouterArc(this);
	}

	public void setEtatSelectioneur() {
		m_etat = new EtatModifierComponent(this);
	}

	public void setEtatPlacerVehicule() 
	{
		m_etat = new EtatPlacerVehicule(this);
	}
	public void setEtatAjouterUregence() 
	{
		m_etat = new EtatAjouterUrgence(this);
	}
	public StrategieAnciennete reqStrategieAnciennetee()
	{
		return unestrategie;
	}
	public boolean isStrategieCourante(String strategie)
	{
		return m_parametres.reqStrategie().equals(strategie);	
	}
	public String reqStrategieCourante()
	{
		return m_parametres.reqStrategie();
	}
	public void asgStrategie(String strategie)
	{
		m_parametres.asgStrategie(strategie);
	}
	public float reqVitesseVehicule()
	{
		return m_parametres.reqVitesseVehicule();
	}
	public void asgVitesseVehicule(float vitesse)
	{
		if (vitesse > 0)
		{
			m_parametres.asgVitesseVehicule(vitesse);
		}
	}
	public boolean isretourPointAttache()
	{
		return m_parametres.reqRetourPointAttache();
	}
	public void asgRetourPointAttache(boolean flag)
	{
		m_parametres.asgRetourPointAttache(flag);
	}
	public int reqMetreParStep()
	{
		return m_echelle.reqMetreParStep();
	}
	public void asgMetreParStep(int value)
	{
		if (value  > 0)
		{
			m_echelle.setMetreParStep(value);
		    updaterCarte();
		}
	}
	
	public Carte reqCarte() 
	{
		return m_carte;
	}
	
	public void ajouterNoeud(int positionX, int positionY) 
	{
		Position position = new Position((float) positionX, (float) positionY);
		position = m_grille.reqPositionEnMetre(position);
		if (m_carte.reqNoeud(position) == null) 
		{
			m_carte.ajouterNoeud(position);
		}	
		

		} 

	public void ajouterArc(Noeud noeudSource, Noeud noeudDest) 
	{
		m_carte.ajouterArc(noeudSource, noeudDest);
	}

	public Noeud reqNoeud(int positionX, int positionY) 
	{
		return m_carte.reqNoeud(m_grille.reqPositionEnMetre(new Position((float) positionX, (float) positionY)));
	}

	public void deplacerNoeud(Noeud noeud, int positionX, int positionY) 
	{
		Position nouvellePosition = new Position((float) positionX, (float) positionY);
		nouvellePosition = m_grille.reqPositionEnMetre(nouvellePosition);
		
		if (nouvellePosition.reqPositionX() >= 0 && nouvellePosition.reqPositionY() >= 0)
		{
			m_carte.deplacerNoeud(noeud, nouvellePosition);
		}
	}
	
	public void updaterCarte()
	{
		for(Noeud noeud : m_carte.reqListeNoeuds())
		{
			m_carte.deplacerNoeud(noeud, m_grille.reqUpdatedPosition(noeud.reqPosition()));
		}
	}

	public Arc reqArc(int positionX, int positionY) 
	{
		return m_carte.reqArc(m_grille.reqPositionEnMetre(new Position((float) positionX, (float) positionY)));
	}

	public boolean existeComponent(int positionX, int positionY) 
	{
		if (this.reqNoeud(positionX, positionY) != null || this.reqArc(positionX, positionY) != null) 
		{
			return true;
		}

		else 
		{
			return false;
		}
	}

	public void supprimer_component() 
	{
		Noeud noeud = m_etat.reqNoeudSelectione();

		if (noeud != null) 
		{
			System.out.println("Entrain de supprimer un noeud");
			m_carte.enleverNoeud(noeud);
		}

		else 
		{
			Arc arc = m_etat.reqArcSelectione();
			if (arc != null) 
			{
				System.out.println("Entrain de supprimer un arc");
				m_carte.enleverArc(arc);
			}
		}
	}
	
	public String reqPositionDescription(int posX, int posY)
	{
		Position position = m_grille.reqPositionEnMetre(new Position(posX, posY));
		int positionX = Math.round(position.reqPositionX());
		int positionY = Math.round(position.reqPositionY());
		
		if (positionX/1000 > 1 || positionY/1000 > 1)
		{
			return "<html>Abscisse : "+ positionX/1000 + "Km<br/> Ordonn�e : " + positionY/1000 +"Km</html>";
		}
		return "<html>Abscisse : " + positionX + "m<br/> Ordonn�e : " + positionY + "m</html>";
	}
	
	public Grille reqGrille() 
	{
		return m_grille;
	}
	
	public void asgVehiculeUrgence(Noeud noeud) 
	{
		m_vehicule.asgPointAttache(noeud);
	}

	public void asgUrgence(Noeud noeud) 
	{
		m_urgence.asgNoeudCible(noeud);
	}

	public Position reqPositionVehicule() 
	{
		return m_vehicule.reqPosition();
	}
	
	public Position reqPositionUrgence() 
	{
		return m_urgence.reqNoeudCible().reqPosition();
	}

	
	public float reqZoom()
	{
		return m_zoom.reqZoom();
	}
	
	public String augmenteZoom()
	{
		return (int)(m_zoom.augmenteZoom()*100) + "%";
	}
	public String diminueZoom()
	{
		return (int)(m_zoom.diminueZoom()*100)+ "%";
	}

	public void supprimerUrgence(Urgence uneUrgence) 
	{

		m_strategie.reqListeUrgence().remove(uneUrgence);
	}

	public void lancerSimulation() {

		if (!m_strategie.reqListeUrgence().isEmpty()) {
			
			m_vehicule.AllerVers(m_strategie.reqUrgencActuelle()
					.reqNoeudCible());
			m_strategie.traiterUrgencAtuelle();
		}

	}

	public void terminerSimulation() 
	{
		m_vehicule.allerPortAttache();
	}

	public void declencherUrgence(Noeud noeudcible, int p_priorite) 
	{
		m_urgence = new Urgence(noeudcible, p_priorite);
		m_strategie.reqListeUrgence().add(m_urgence);
		System.out.println("une urgence a eté declenché Position "
				+ noeudcible.reqPosition().reqPositionX() + ","
				+ noeudcible.reqPosition().reqPositionY());
	}


	public void SauvegarderEtatActuel() 
	{
		if (m_etatsimu.reqListeEtatSimu().contains(this))
			System.out.println("cette simulation a eté dejas enregistré");
		else
			m_etatsimu.ajouterEtatSimu(this);
	}

	public void annuler() 
	{
		if (!m_etatsimu.reqListeEtatSimu().isEmpty()) 
		{
			m_etatsimu.ajouterEtatsuivantSimu(m_etatsimu.reqListeEtatSimu().peek());
			// TODO this = m_etatsimu.reqListeEtatSimu().peek();
			m_etatsimu.reqListeEtatSimu().poll();
		}

	}

	public void retablir() {

		if (!m_etatsimu.reqListeEtatsuivantSimu().isEmpty()) 
		{
			m_etatsimu.ajouterEtatSimu(m_etatsimu.reqListeEtatsuivantSimu().peek());
			// TODO this = m_etatsimu.reqListeEtatsuivantSimu().peek();
			m_etatsimu.reqListeEtatsuivantSimu().poll();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		m_etat.mouseClicked(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		m_etat.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		m_etat.mouseExited(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		m_etat.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		m_etat.mouseReleased(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		m_etat.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		m_etat.mouseMoved(e);
	}

}
