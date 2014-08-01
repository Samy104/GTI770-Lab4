package func;

import javax.annotation.Generated;

import Jama.Matrix;

/*
 * Cette classe contient seulement les fonctions de Fisherfaces.
 * 
 */
public class Fisherfaces {
	
	
	private Fisherfaces(){}

	public static Matrix ScatterMatrix(Matrix W)
	{
		return func.Fonctions.GenerateScatterMatrix(W);
	}
	
	public static Matrix WithinScatterMatrix(Matrix W)
	{
		
		Matrix mean = new Matrix(W.getRowDimension(),W.getColumnDimension());
		//System.out.println("Mean column is " + W.getColumnDimension());
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
		return mean.times(mean.transpose());
	}
	
	public static Matrix BetweenScatterMatrix(Matrix W)
	{
		Matrix mean = new Matrix(W.getRowDimension(),W.getColumnDimension());
		//System.out.println("Mean column is " + W.getColumnDimension());
		int n = W.getColumnDimension() / func.Fonctions.numberClasses;
		
		
			// For every chunk of 40 elements get the same class element within each
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
		return mean.times(mean.transpose());
	}
	
	public static Matrix WPCA(Matrix W)
	{
		System.out.println("Scatter : " + ScatterMatrix(W).getRowDimension()+ "  "+ScatterMatrix(W).getColumnDimension());
		System.out.println("W : " + W.getRowDimension()+ "  "+W.getColumnDimension());
		return W.times(ScatterMatrix(W)).times(W.transpose());

	}
	
	public static Matrix WFLD(Matrix W)
	{
		Matrix wpca = WPCA(W);
		//wpca.print(2, 5);
		//System.out.println("WPCA : " + wpca.getRowDimension()+ "  "+wpca.getColumnDimension());
		System.out.println("BetweenScatterMatrix : " + BetweenScatterMatrix(W).getRowDimension()+ "  "+BetweenScatterMatrix(W).getColumnDimension());
		System.out.println("WithinScatterMatrix : " + WithinScatterMatrix(W).getRowDimension()+ "  "+WithinScatterMatrix(W).getColumnDimension());
		//System.out.println("W : " + W.getRowDimension()+ "  "+W.getColumnDimension());
		//System.out.println("W.times(wpca): " + W.times(wpca).getRowDimension() + "  "+W.times(wpca).getColumnDimension());
		
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
	
	public static Matrix WOPT(Matrix W, boolean entrainement)
	{
		// Method 1
		//Matrix wopt = W.times(BetweenScatterMatrix(W).times(W.transpose()));
		//int col = func.Fonctions.getMaxDet(W);
		//return wopt.getMatrix(0, wopt.getRowDimension()-1, col, col);
		// If it is training, generate the scatter matrices
		// Method 2
		System.out.println("WCPA T : " + WPCA(W).transpose().getRowDimension() + "  " + WPCA(W).transpose().getColumnDimension());
		System.out.println("WFLD T : " + WFLD(W).transpose().getRowDimension() + "  " + WFLD(W).transpose().getColumnDimension());
		return WPCA(W).transpose().times(WFLD(W).transpose()).transpose();
	}
	
	/*
	private static Matrix GetClass(int i, Matrix w) {
		int maxShifts = w.getColumnDimension()/func.Fonctions.numberClasses;
		Matrix newMatrix = new Matrix(w.getRowDimension(),maxShifts);
		int currentShift = 0;
		for(int currentNewPosition = i%(w.getColumnDimension()/func.Fonctions.numberClasses); currentShift < maxShifts; currentNewPosition++)
		{
			newMatrix.setMatrix(0, w.getRowDimension()-1, currentNewPosition, currentNewPosition, 
					w.getMatrix(0, w.getRowDimension()-1, currentShift, currentShift));
			currentShift+=func.Fonctions.numberClasses;
		}
		return newMatrix;
	}
   */
}
