package func;

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
		
		System.out.println(W.getMatrix(0, 0, 0 , W.getColumnDimension()-1).times(ScatterMatrix(W).times(W.getMatrix(0, 0,0 , W.getColumnDimension()-1).transpose())).det());
		Matrix wpca = new Matrix(0, W.getRowDimension());
		
		for(int row = 0; row < W.getRowDimension(); row++)
		{
			//Matrix wpca = W.times(ScatterMatrix(W).times(W.transpose()));
		}
		//System.out.println("Rows " + wpca.getRowDimension() + " Cols " + wpca.getColumnDimension());
		//int col = func.Fonctions.getMaxDet(wpca);		
		//return wpca.getMatrix(0, wpca.getRowDimension()-1, col, col);
		return null;
	}
	
	public static Matrix WFLD(Matrix W)
	{
		Matrix wpca = WPCA(W);
		Matrix num = W.times(wpca.times(BetweenScatterMatrix(W).times(wpca.transpose().times(W.transpose()))));
		Matrix denum = W.times(wpca.times(WithinScatterMatrix(W).times(wpca.transpose().times(W.transpose()))));
		// Reminder X/Y = X*Y^⁻1
		Matrix wfld = num.times(denum.transpose());
		int col = func.Fonctions.getMaxDet(wfld);
		wfld = wfld.getMatrix(0, wfld.getRowDimension()-1, col, col);
		
		return wfld;
	}
	
	public static Matrix WOPT(Matrix W)
	{
		Matrix wopt = W.times(BetweenScatterMatrix(W).times(W.transpose()));
		System.out.println(wopt.getColumnDimension()+"  "+wopt.getRowDimension());
		int col = func.Fonctions.getMaxDet(W);
		return wopt.getMatrix(0, wopt.getRowDimension()-1, col, col);
	}
}
