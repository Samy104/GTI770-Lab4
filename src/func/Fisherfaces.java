package func;

import Jama.Matrix;

/**
 * @author Christopher Larivière, Samy Lemcelli
 * Cette classe contient seulement les fonctions de Fisherfaces.
 * 
 */
public class Fisherfaces {
	
	
	private Fisherfaces(){}

	/**
	 * Gènere une matrice générique dispersée
	 * @param la matrice W
	 * @return Matrice St
	 */
	
	public static Matrix ScatterMatrix(Matrix W)
	{
		return func.Fonctions.GenerateScatterMatrix(W);
	}
	
	/**
	 * Calcule le Sw pour la matrice passée en paramètre
	 * @param Matrice passée en paramètre
	 * @return Matrice Sw
	 */
	
	public static Matrix WithinScatterMatrix(Matrix W)
	{
		
		Matrix mean = new Matrix(W.getRowDimension(),W.getColumnDimension());
		int n = W.getColumnDimension() / func.Fonctions.numberClasses;
		
		for(int row = 0; row < W.getRowDimension(); row++)
		{
			double rowTotal = 0;
			double totalClass[] = new double[func.Fonctions.numberClasses];
			for(int col = 0; col < W.getColumnDimension(); col++)
			{
				rowTotal += W.get(row,col);
				totalClass[col%(func.Fonctions.numberClasses)] += W.get(row,col);
			}
			rowTotal = rowTotal/n;

			for(int col = 0; col < W.getColumnDimension(); col++)
			{
				if(col < totalClass.length)
				{
					totalClass[col] /= n;
				}
				mean.set(row,col,n*(W.get(row, col)-totalClass[col%(func.Fonctions.numberClasses)]));
					
			}
			
		}
		return mean.transpose().times(mean);
	}
	
	/**
	 * Calcule la matrice Sb de la matrice passée en paramètre
	 * @param Matrice passée en paramètre
	 * @return Matrice Sb
	 */
	
	public static Matrix BetweenScatterMatrix(Matrix W)
	{
		Matrix mean = new Matrix(W.getRowDimension(),W.getColumnDimension());
		int n = W.getColumnDimension() / func.Fonctions.numberClasses;
		
		// Pour chaque 40 éléments récupère la même élément de la classe.	
		for(int row = 0; row < W.getRowDimension(); row++)
		{
			double rowTotal = 0;
			double totalClass[] = new double[func.Fonctions.numberClasses];
			for(int col = 0; col < W.getColumnDimension(); col++)
			{
				rowTotal += W.get(row,col);
				totalClass[col%(func.Fonctions.numberClasses)] += W.get(row,col);
			}
			rowTotal = rowTotal/n;

			for(int col = 0; col < W.getColumnDimension(); col++)
			{
				if(col < totalClass.length)
				{
					totalClass[col] /= n;
				}
				mean.set(row,col,(totalClass[col%(func.Fonctions.numberClasses)]-rowTotal));
			}
			
		}
		return mean.transpose().times(mean);
	}
	
	/**
	 * Calcule la matrice Wpca nécessaire pour l'application
	 * @param La matrice W
	 * @return une matrice Wpca
	 */
	
	public static Matrix WPCA(Matrix W)
	{
		System.out.println("Scatter : " + ScatterMatrix(W).getRowDimension()+ "  "+ScatterMatrix(W).getColumnDimension());
		System.out.println("W : " + W.getRowDimension()+ "  "+W.getColumnDimension());
		return W.times(ScatterMatrix(W)).times(W.transpose());
	}
	
	/**
	 * Calcule la matrice "Fishers linear discriminant" pour la matrice passée en paramètre
	 * @param Matrice W
	 * @return Matrice Wfld
	 */
	
	public static Matrix WFLD(Matrix W)
	{
		Matrix wpca = WPCA(W);
		
		Matrix num = W.transpose()
				.times(wpca.transpose())
				.times(BetweenScatterMatrix(W))
				.times(wpca)
				.times(W);
		
		Matrix denum = W.transpose()
				.times(wpca.transpose())
				.times(WithinScatterMatrix(W))
				.times(wpca)
				.times(W);
		
		System.out.println("Num : " + num.getRowDimension() + "   " + num.getColumnDimension());
		System.out.println("Denum : " + denum.getRowDimension() + "   " + denum.getColumnDimension());
		return denum.transpose().times(num);

	}
	
	/**
	 * Calcule la matrice Wopt (W optimale) de la matrice passée en paramètre
	 * @param Matrice W
	 * @return la matrice Wopt
	 */
	
	public static Matrix WOPT(Matrix W)
	{
		return (W.times(BetweenScatterMatrix(W)).times(W.transpose()))
				.times(W.times(WithinScatterMatrix(W)).times(W.transpose()).transpose());
	}
}
