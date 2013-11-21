package presentation;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.event.MouseListener;

import domainePartie1.Default;
import domainePartie1.Simulateur;

public class InterfaceGraphique extends JFrame 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BarBoutons bar;
	private Menu menu;
	private CarteGraphique m_carteGraphique;
	private EditPanel m_panneauEdition;
	
    private static Simulateur m_simulateur;
    private Afficheur m_afficheur;
    private JDialog dialog;

	public InterfaceGraphique(Simulateur p_simulateur, Afficheur p_afficheurGraphique) 
	{
		super("Intervensim");
		
		
		
		m_afficheur = p_afficheurGraphique;
		m_simulateur = p_simulateur;
		
		
		// Ajout de la carte graphique au centre
		m_carteGraphique = new CarteGraphique(m_afficheur, m_simulateur);
		
		JScrollPane scroller = new JScrollPane(m_carteGraphique);
		scroller.setPreferredSize(new Dimension(Default.CARTE_WIDTH, Default.CARTE_HEIGHT));
		getContentPane().add(scroller);
		
		
		
		// Ajout Panneau Edition
		m_panneauEdition = new EditPanel(m_simulateur);
		getContentPane().add(m_panneauEdition, BorderLayout.WEST);
		
		dialog = new JDialog(this);
		dialog.setPreferredSize(new Dimension(30, 30));
		m_panneauEdition.add(dialog);
		
		// Ajout du menu et de la barre des buttons
		menu = new Menu();
		bar = new BarBoutons(m_carteGraphique, m_simulateur);

		
		getContentPane().add(bar, BorderLayout.NORTH);
		this.setJMenuBar(menu);
	
		
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void ajouterCarteListener(MouseListener listener)
	{
		m_carteGraphique.addMouseListener(listener);
	}
	
	public void rafraichirCarte() 
	{
		m_carteGraphique.repaint();
	}
}
