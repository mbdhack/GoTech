/**
* Cette classe gere les urgences par anciennete
* 
*
* Permet de determiner la position finale d'un noeud en arrondissant au plus proche
* point de la grille
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

import domaine.simulateur.Default;
import domaine.simulation.urgence.Urgence;

public class StrategieAnciennete extends StrategieGestion 
{

	public StrategieAnciennete(ArrayList<Urgence> listeUrgence, ArrayList<Urgence> listUrgenceTraitee, ArrayList<Urgence> listeUrgenceNonAccessible) 
	{
		super(listeUrgence, listUrgenceTraitee, listeUrgenceNonAccessible);
	}
	
	
	/**
	* Retourne l'urgence la plus anciennce
	* 
	* @return retourne l'urgence la plus anciennce 
	* 
	*/
	public Urgence reqProchaineUrgence()
	{
		if (super.reqProchaineUrgence() == null && !this.reqListeUrgence().isEmpty())
		{
			this.asgProchaineUrgence(this.reqListeUrgence().get(0));
		}
		
		return super.reqProchaineUrgence();
	}

	public String toString()
	{
		return Default.STRATEGIE_ANC;
	}

}