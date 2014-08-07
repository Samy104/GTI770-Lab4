import java.util.Arrays;
import java.util.Comparator;

import Jama.Matrix;

/**
 * @author Samy Lemcelli, Christopher Larivière
 * La classe PCA qui nous calcule notre PCA.
 */

public class PCA {

	Matrix xMatrix = null;
	
	public Matrix xBar = null;
	public Matrix R = null;
	public Matrix wk = null;
	
	public Matrix matriceDeCovariance = null;
	
	Matrix principauxVecteurs = null;
	Matrix Z = null;
	Double[][] OrderedMatrices;
	

	/**
	 * Constructeur principale de notre classe
	 * @param la Matrice utilisée par la classe
	 */
	
	public PCA (Matrix main)
	{
		this.xMatrix = main;
	}
	
	/**
	 * Exécute la tâche principal du PCA.
	 * La calcule.
	 */
	
	public void Calculate()
	{
		
		this.xBar = func.Fonctions.CalculateCenteredMatrix(xMatrix);
		
		Matrix swapped = getSwappedDIAGMatrix(diagonal(xBar));
		calculatePrincipauxVec(swapped,xBar);
		
		this.wk = principauxVecteurs.times(getXbar().transpose());
		this.Z = this.wk.times(getXbar());
		
	}
	
	/**
	 * Calcule les K principaux vecteurs avec un alpha qui représente 0.98 ou 98 % (pourcent) des données
	 * @param La matrice pour calculer les vecteurs propres
	 * @param x
	 */
	
	public void calculatePrincipauxVec(Matrix swap,Matrix x){
		int i = 0;
		
		while(i < swap.getRowDimension() && !getKPrincipauxVecteurs(i, swap))	{
			i++;			
		}
		i++;
		
		System.out.println("Number of principaux vectors is " + i);
		Matrix vecProp = vecteurPropre(x);
		principauxVecteurs = getFirstIPrincVec(vecProp, i);
	}
		
	/**
	 * Calcule la première vecteur propre du PCA
	 * @param Matrice x
	 * @param nombre de rangée nécessaire dans matrice
	 * @return une matrice de Principaux Vecteurs
	 */
	
	private Matrix getFirstIPrincVec(Matrix propre, int i) {
		Matrix vecTot = new Matrix(i,propre.getColumnDimension());

		for(int currentPixel = 0; currentPixel < i; currentPixel++)
		{
			// Prend la 'i' courante et récupère cette position du tableau Ordered Matrices et ajoute la à la matrice.
			int currentPrincipaux = OrderedMatrices[currentPixel][1].intValue();
			
			vecTot.setMatrix(currentPixel, currentPixel, 0, propre.getColumnDimension()-1, 
					propre.getMatrix((int)OrderedMatrices[currentPrincipaux][1].intValue(), (int)OrderedMatrices[currentPrincipaux][1].intValue(), 
									0, propre.getColumnDimension()-1));
		}
		
		return vecTot;
	}

	/**
	 * Retourne la matrice des vecteurs propres de la matrice passée en paramètre
	 * @param matrice x
	 * @return matrice des vecteurs propres 
	 */
	
	public Matrix vecteurPropre(Matrix x){		
		return x.eig().getV();	
	}
	
	/**
	 * Retourne la matrice diagonal de la matrice passée en paramètre
	 * @param matrice x
	 * @return la diagonal de la matrice passée en paramètre
	 */
	
	public Matrix diagonal(Matrix x){
		return x.eig().getD();
	}
	
	/**
	 * Retourne la matrice des vecteurs propres transposée de la matrice passée en paramètre
	 * @param matrice x 
	 * @return Matrice des vecteurs propres transposée
	 */
	
	public Matrix vecPTranspose(Matrix x){
		return vecteurPropre(x).transpose();
	}
	
	/**
	 * Retourne xBar (déjà calculée)
	 * N.B: il ne sera pas recalculer, sinon, la matrice retournera null
	 * @return matrice xbar
	 */
	public Matrix getXbar(){
		return this.xBar;
	}
	
	/**
	 * La matrice des principaux vecteurs (déjà calculer)
	 * N.B: la matrice doit être calculer, sinon, la matrice retournera null.
	 * @return
	 */
	
	public Matrix getPrincipauxVecteurs()
	{
		return this.principauxVecteurs;
	}
	
	/**
	 * Les poids vectorielles
	 * N.B: la matrice doit être calculer, sinon, la matrice retournera null.
	 * @return
	 */
	
	public Matrix getWk()
	{
		return this.wk;
	}
	
	/**
	 * Retourne la matrice xbar transposée 
	 * @return matrice xbar transposée
	 * N.B: la matrice doit être calculer, sinon, la matrice retournera null.
	 */
	
	public Matrix getXbarTranspose(){
		return this.xBar.transpose();
	}
	
	/**
	 * Mets les diagonal en ordre décroissant.
	 * @param la matrice à replacer en ordre
	 * @return
	 */
	
	public Matrix getSwappedDIAGMatrix(Matrix x)
	{
		Matrix exit = new Matrix(x.getRowDimension(),x.getColumnDimension());
		
		OrderedMatrices = new Double[x.getRowDimension()][2];
		
		for(int i = 0; i < x.getRowDimension(); i++)
		{
			
			OrderedMatrices[i][0] = Math.abs(x.get(i,i));
			OrderedMatrices[i][1] = (double) i;
		}
		Arrays.sort(OrderedMatrices, 
				new Comparator<Double[]>() {
			        @Override
			        public int compare(Double[] o1, Double[] o2) {
			            return o2[0].compareTo(o1[0]);
			        }
			    });
		
		for(int i = 0; i < x.getRowDimension(); i++)
		{
			exit.set(i, i, OrderedMatrices[i][0]);
		}
		
		return exit;
	}
	
	/**
	 * Calcule les principaux vecteurs de la matrice passée en paramètre
	 * @param constante k
	 * @param matrice x
	 * @return vrai si cela a fonctionné, faux sinon.
	 */
	
	public boolean getKPrincipauxVecteurs(int k, Matrix from)
	{
		boolean decision = false;
		
		double num = 0;
		double denum = 0;
		
		for(int i = 0; i < from.getRowDimension(); i++)
		{
			if(i <= k)
			{
				num += from.get(i, i);
			}
			denum += from.get(i, i);
		}
		decision = (num/denum >= 0.98) ? true : false;
		
		return decision;
	}
	
	/**
	 * Retourne la matrice projetée Z  calculer auparavant.
	 * @return matrice projetée Z
	 * N.B: la matrice doit être calculer, sinon, la matrice retournera null.
	 */
	
	public Matrix getProjectedMatrix(){
		return this.Z;
	}
}
