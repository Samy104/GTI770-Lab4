package func;

import javax.annotation.Generated;

import Jama.Matrix;

/*
 * Cette classe contient seulement les fonctions de Fisherfaces.
 * 
 */
public class Fisherfaces {
	
	private static Matrix betweenScatterEntrainer = null;
	private static Matrix withinScatterEntrainer = null;
	private static Matrix[] scatterEntrainer = new Matrix[func.Fonctions.numberClasses];
	
	private Fisherfaces(){}

	public static Matrix ScatterMatrix(Matrix W)
	{
		return func.Fonctions.GenerateScatterMatrix(W);
	}
	
	public static Matrix WithinScatterMatrix(Matrix W)
	{
		Matrix mean = new Matrix(W.getColumnDimension(),W.getRowDimension());
		int n = W.getColumnDimension() / func.Fonctions.numberClasses;
		
		for(int col = 0; col < W.getColumnDimension(); col++)
		{
			// For every chunk of 40 elements get the same class element within each
			double totalClass = 0;
			for(int groupIndex = col%n; groupIndex < W.getColumnDimension(); groupIndex += func.Fonctions.numberClasses)
			{
				totalClass += func.Fonctions.mean(W, groupIndex);
			}
			for(int row = 0; row < W.getRowDimension(); row++)
			{
				mean.set(col,row,n*(W.get(row, col)-totalClass/n));
			}
		}
		
		return mean.transpose().times(mean);
	}
	
	public static Matrix BetweenScatterMatrix(Matrix W)
	{
		Matrix mean = new Matrix(W.getColumnDimension(),1);
		int n = W.getColumnDimension() / func.Fonctions.numberClasses;
		
		for(int col = 0; col < W.getColumnDimension(); col++)
		{
			// For every chunk of 40 elements get the same class element within each
			double totalClass = 0;
			for(int groupIndex = col%n; groupIndex < W.getColumnDimension(); groupIndex += func.Fonctions.numberClasses)
			{
				totalClass += func.Fonctions.mean(W, groupIndex);
			}
			mean.set(col,0,n*(totalClass/n-func.Fonctions.meanTotal(W)));
		}
		
		return mean.transpose().times(mean);
	}
	
	public static Matrix WPCA(Matrix W)
	{
		
		/*Matrix wpca = new Matrix(0, W.getRowDimension());
		
		for(int row = 0; row < W.getRowDimension(); row++)
		{
			System.out.println(W.getMatrix(0, 0, 0 , W.getColumnDimension()-1).times(ScatterMatrix(W).times(W.getMatrix(0, 0,0 , W.getColumnDimension()-1).transpose())).det());
		}*/
		
		//Matrix wpca = W.times(ScatterMatrix(W).times(W.transpose()));
		//System.out.println("Rows " + wpca.getRowDimension() + " Cols " + wpca.getColumnDimension());
		//int col = func.Fonctions.getMaxDet(wpca);
		double det=-9999;
		Matrix bestClass = null;
		for(int count = 0; count < func.Fonctions.numberClasses;count++)
		{
			Matrix oneClass = GetClass(count, W);
			Matrix wpca = oneClass.times(scatterEntrainer[count].times(oneClass.transpose()));
			double currentDet = wpca.det();
			det = (currentDet > det) ? currentDet : det;
			bestClass = (currentDet > det) ? wpca : bestClass;
		}
		System.out.println("Le meilleur det est : " + det);	
		return bestClass;
	}
	
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

	public static Matrix WFLD(Matrix W)
	{
		Matrix wpca = WPCA(W);
		
		double det=-9999;
		Matrix bestClass = null;
		for(int count = 0; count < func.Fonctions.numberClasses;count++)
		{
			Matrix oneClass = GetClass(count, W);
			Matrix num = oneClass.times(wpca.times(betweenScatterEntrainer.times(wpca.transpose().times(oneClass.transpose()))));
			Matrix denum = oneClass.times(wpca.times(withinScatterEntrainer.times(wpca.transpose().times(oneClass.transpose()))));
			
			Matrix wfld = num.times(denum.transpose());
			double currentDet = wfld.det();
			det = (currentDet > det) ? currentDet : det;
			bestClass = (currentDet > det) ? wfld : bestClass;
		}
		System.out.println("Le meilleur det est : " + det);
		return bestClass;
	}
	
	public static Matrix WOPT(Matrix W, boolean entrainement)
	{
		// Method 1
		//Matrix wopt = W.times(BetweenScatterMatrix(W).times(W.transpose()));
		//int col = func.Fonctions.getMaxDet(W);
		//return wopt.getMatrix(0, wopt.getRowDimension()-1, col, col);
		// If it is training, generate the scatter matrices
		if(entrainement)
		{
			for(int count = 0; count < func.Fonctions.numberClasses; count++)
			{
				scatterEntrainer[count] = ScatterMatrix(GetClass(count,W));
			}
			
			betweenScatterEntrainer = BetweenScatterMatrix(W);
			withinScatterEntrainer = WithinScatterMatrix(W);
		}
		// Method 2
		return WPCA(W).transpose().times(WFLD(W).transpose()).transpose();
	}
	
	
}
