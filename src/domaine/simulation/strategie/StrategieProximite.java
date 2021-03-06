/**
* Cette gere les urgence selon leur proximite
* 
* 
* Permet de trier la liste des urgences selon leur proximite
*
*
* @version 1.0
*
* @author GoTech
*
*
*/

package domaine.simulation.strategie;

import java.util.ArrayList;

import domaine.reseau.Carte;
import domaine.reseau.Noeud;
import domaine.simulateur.Default;
import domaine.simulation.urgence.Urgence;

public class StrategieProximite extends StrategieGestion 
{

	public StrategieProximite(ArrayList<Urgence> listeUrgence, ArrayList<Urgence> listeUrgenceTraitee,ArrayList<Urgence> listeUrgenceNonAccessible)
	{
		super(listeUrgence, listeUrgenceTraitee,listeUrgenceNonAccessible);
	}
	
	/**
	* Retourne le noeud ayant l'urgence la plus proche
	* 
	* @return l'urgence la plus proche
	*
	*/
	public Urgence reqProchaineUrgence( Carte carte)
	{
		Noeud noeudPlusProche = carte.plusProche(this.reqProchaineUrgence().reqNoeudCible(), listeDesNoeudAyantUneUrgence() );
		int trouve = 0;
		
		Urgence prochaineUrgence = new Urgence(noeudPlusProche);
		for (int i = 0; ((i < this.reqListeUrgence().size()) || (trouve == 0)) ; i++)
		{
			if (noeudPlusProche == this.reqListeUrgence().get(i).reqNoeudCible())
			{
				prochaineUrgence = this.reqListeUrgence().get(i);
				trouve = 1;
			}
		}
		
		this.asgProchaineUrgence(prochaineUrgence);
		
		return prochaineUrgence;
		
	}
	
	/**
	* Liste tous les noeuds ayant une urgence
	* 
	* @return la liste des noeuds ayant une urgence
	*
	*/
	public ArrayList<Noeud> listeDesNoeudAyantUneUrgence()
	{
		ArrayList<Noeud> listNoeudUrgence = new ArrayList<Noeud>();
		if (this.reqListeUrgence().size() !=0)
		{
			for(int i = 0; i< this.reqListeUrgence().size(); i++)
			{
				listNoeudUrgence.add(this.reqListeUrgence().get(i).reqNoeudCible());
			}
		}
			
		return listNoeudUrgence;
	}
	
	
	public void traiterUrgenceActuelle() 
	{
		if (this.reqListeUrgence().size() != 0) 
		{
			Urgence urgence = this.reqProchaineUrgence();
			this.asgProchaineUrgence(urgence);
			this.traiterUrgenceAtuelle();
		} 
		else
		{
			System.out.println("il ya plus d'ergence a Traiter");
		}
			
	}
	
	public String toString() 
	{
		return Default.STRATEGIE_PROX;
	}

	
}
