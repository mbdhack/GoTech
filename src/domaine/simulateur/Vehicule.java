package domaine.simulateur;

import java.util.ArrayList;

import domaine.reseau.Arc;
import domaine.reseau.Carte;
import domaine.reseau.Noeud;
import domaine.reseau.Position;
import domaine.simulation.urgence.*;

public class Vehicule {

	private static Vehicule m_vehicule = null;
	private Noeud m_portAttache = null;
	private float m_vitesse = 0;
	private boolean m_retourPointAttache;
	private Position m_position = null;
	private Noeud m_noeudActuel = null;

	private long clock = 0;
	

	private GestionnaireUrgence m_gestionnaireUrgence;
	private Noeud m_noeudDestination = null;
	private Carte m_gps;
	private ArrayList<Noeud> m_itineraireActuel = null;

	private float distanceDuProchainNoeud;

	private float distanceparcourue = 0;
	private int coefficientVitesse = 1;
	private boolean en_traitement = false;
	private int m_tempsTraitementUrgence = 10;
	private int compteurTempsTraitement = 0;
	private int tempsAttente;
	private float directionX;
	private float directionY;
	private double angle = 0;
	private float m_echelleTemps;

	// constructeur priv�e vehicule

	private Vehicule() {

	}

	// Obtenir le v�hicule
	public static Vehicule getInstance() {
		if (m_vehicule == null)
			m_vehicule = new Vehicule();

		return m_vehicule;
	}

	public void lancerMission(GestionnaireUrgence gestionnaire, Carte gps,
			float vitesse, float tempsTraitement, boolean retour,
			float echelleTemps) {
		this.asgNoeudActuel(m_portAttache);
		m_gestionnaireUrgence = gestionnaire;
		m_vitesse = (vitesse * 1000) / 3600;
		m_gps = gps;
		m_tempsTraitementUrgence = Math.round(tempsTraitement * 60);
		m_retourPointAttache = retour;
		m_echelleTemps = 3600 / echelleTemps;
	}

	public void asgPointAttache(Noeud noeud) {
		m_portAttache = noeud;
		m_noeudActuel = m_portAttache;
		m_position = m_portAttache.reqPosition();
	}

	public void asgGestionnaireUrgence(GestionnaireUrgence gps) {
		m_gestionnaireUrgence = gps;
	}

	public void ajusteVitesseVehicule(int coefficient) {
		this.coefficientVitesse = coefficient;
	}

	public Position reqPosition() {
		if (this.m_noeudActuel != null) {

			return this.m_noeudActuel.reqPosition();

		}
		return this.m_position;
	}

	public double reqDirection() {
		return angle;
	}

	public Noeud reqPortAttache() {

		return this.m_portAttache;
	}

	public float reqVistess() {
		return this.m_vitesse;
	}

	public Noeud reqnoeudActuel() {
		return m_noeudActuel;
	}

	public void asgNoeudActuel(Noeud noeud) {
		this.m_noeudActuel = noeud;
		this.m_position = noeud.reqPosition();
	}

	public void asgPosition(Position unePosition) {

		this.m_position = unePosition;
	}

	public void avance(int duree) {
		// ne tiens pas encore compte du chemin

		duree = Math.round(duree * this.m_echelleTemps
				* this.coefficientVitesse);
		if (this.isEnTraitement()) {
			this.continuerTraitement(duree);
		}

		else {

			if (this.isEnChemin()) {
				this.poursuisChemin(duree);
			} else {

				m_noeudDestination = this.m_gestionnaireUrgence
						.reqProchainNoeudATraite();

				if (m_noeudDestination != null) {
					this.initialiseNouvelleUrgence(m_noeudDestination);
				} else {
					this.resteEnEtatDAttente();
				}

			}
		}

		this.m_gestionnaireUrgence.incrementerTempsAttenteUrgence(duree);
		this.clock += duree;
		this.m_gestionnaireUrgence.declencherUrgenceEnAttente(this.clock);
		

	}

	private boolean isEnChemin() {
		return this.m_itineraireActuel != null
				&& !this.m_itineraireActuel.isEmpty();
	}
//<<<<<<< HEAD
//
//	private void initialiseNouvelleUrgence(Noeud destination) {
//		this.m_itineraireActuel = this.m_gps.trouverItineraire(m_noeudActuel,
//				m_noeudDestination);
//		if (this.m_itineraireActuel != null
//				&& !this.m_itineraireActuel.isEmpty()) {
//=======
	private void initialiseNouvelleUrgence(Noeud destination){
		this.m_itineraireActuel = this.m_gps.trouverItineraire(m_noeudActuel, destination);
		if(this.m_itineraireActuel!=null && !this.m_itineraireActuel.isEmpty())
		{

			this.declencherMission();
		} else {
			this.m_gestionnaireUrgence.setUrgenceActuelleNonAccessible();
		}
	}

	private void resteEnEtatDAttente() {
		// ici le vehicule v�rifie d'abord si sa strat�gie est de retourner au
		// port d'attache.
		// Si c'est le cas, il v�rifie s'il est au port d'attache, si oui, il ne
		// fait rien
		// Sinon il rentre au port.
		// Si jamais sa strat�gie n'est pas de retourn� au port d'attache, il
		// reste sur place
		if (m_retourPointAttache && !isNoeudActuelPortAttache()) {
			m_noeudDestination = m_portAttache;
			this.m_itineraireActuel = this.m_gps.trouverItineraire(
					m_noeudActuel, m_noeudDestination);
			if (!this.m_itineraireActuel.isEmpty()
					&& this.m_itineraireActuel != null) {
				this.declencherMission();

			}
		}
	}

	private void continuerTraitement(int duree) {

		compteurTempsTraitement += duree;

		if (compteurTempsTraitement >= this.m_tempsTraitementUrgence * 1000) {
			this.finirTraitement();
		}

	}

	private void declencherMission() {
		Noeud noeud = this.m_itineraireActuel.get(0);
		Arc arc = new Arc(m_noeudActuel, noeud); // Pour des raisons de calcul,
													// on cr�e un nouvel arc
		distanceDuProchainNoeud = arc.reqLongueur();
		float y2 = m_itineraireActuel.get(0).reqPosition().reqPositionY();
		float x2 = m_itineraireActuel.get(0).reqPosition().reqPositionX();
		float x1 = this.m_position.reqPositionX();
		float y1 = this.m_position.reqPositionY();
		this.angle = Math.atan2(y2 - y1, x2 - x1);
		if (x1 - x2 > 0) {
			this.angle += Math.PI;

		}
		this.directionX = (float) Math.cos(Math.atan2(y2 - y1, x2 - x1));
		this.directionY = (float) Math.sin(Math.atan2(y2 - y1, x2 - x1));
		this.m_noeudActuel = null;

	}

	private void poursuisChemin(int duree) {

		this.asgNouvellePositionVoiture(duree);

	}

	private void asgNouvellePositionVoiture(int duree) {

		Position position = null;
		float distance = this.m_vitesse * duree / 1000;

		if (distanceDuProchainNoeud < distance) {
			distanceparcourue += distanceDuProchainNoeud;
			this.arriverAuProchainNoeud();

		} else {
			distanceparcourue += distance;
			float x1 = this.m_position.reqPositionX();
			float y1 = this.m_position.reqPositionY();
			float newPositionX = x1 + distance * directionX;
			float newPositionY = y1 + distance * directionY;
			position = new Position(newPositionX, newPositionY);
			this.asgPosition(position);
			distanceDuProchainNoeud -= distance;
		}

	}

	private void arriverAuProchainNoeud() {
		Noeud noeud = this.m_itineraireActuel.get(0);
		this.asgNoeudActuel(noeud);
		this.m_itineraireActuel.remove(noeud);
		distanceDuProchainNoeud = 0;

		if (this.isDestinationAtteinte()) {
			if (this.isNoeudActuelEnUrgence()) {
				this.entreEnTraitement();
				this.m_gestionnaireUrgence.traiterUrgenceActuelle();

			} else {
				this.idle();
			}

		} else {
			this.declencherMission();
		}

	}

	private boolean isDestinationAtteinte() {
		return this.m_noeudDestination.equals(m_noeudActuel);
	}

	private boolean isNoeudActuelEnUrgence() {
		if (m_noeudActuel == null
				|| m_gestionnaireUrgence.reqProchainNoeudATraite() == null) {
			return false;
		}
		return m_noeudActuel.equals(m_gestionnaireUrgence
				.reqProchainNoeudATraite());
	}

	private void entreEnTraitement() {
		this.compteurTempsTraitement = 0;
		this.en_traitement = true;
		this.m_noeudActuel.setEnTraitement();
		this.angle = 0;
	}

	private void idle() {
		this.m_itineraireActuel = null;
		this.m_noeudDestination = null;
		this.angle = 0;
		this.directionX = 0;
		this.directionY = 0;
	}

	private void finirTraitement() {
		// TODO
		m_noeudActuel.setTraitee();
		this.compteurTempsTraitement = 0;
		this.en_traitement = false;
		this.idle();

	}

	private boolean isNoeudActuelPortAttache() {
		if (m_noeudActuel == null) {
			return false;
		} else {
			return m_noeudActuel.equals(m_portAttache);
		}
	}

	public boolean isEnTraitement() {
		return this.en_traitement;

	}

	public int reqTempAttente() {
		return this.tempsAttente;
	}

	public int reqNombreUrgence() {

		return this.m_itineraireActuel.size();
	}

	public float reqDistanceparcouru() {
		return this.distanceparcourue;
	}

	public void reset() {
		this.asgNoeudActuel(m_portAttache);
		this.distanceparcourue = 0;
		this.en_traitement = false;
		this.angle = 0;
		this.m_itineraireActuel = null;
		this.compteurTempsTraitement = 0;

	}

}