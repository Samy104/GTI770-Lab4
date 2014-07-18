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
	
	public static Matrix BetweenScatterMatrix(Matrix W)
	{
		Matrix scatterB = null;
		
		
		
		return scatterB;
	}
	
	public static Matrix WithinScatterMatrix(Matrix W)
	{
		Matrix scatterO = null;
		
		
		
		return scatterO;
	}
	
	public static Matrix WPCA(Matrix W)
	{
		Matrix wpca = W.times(ScatterMatrix(W).times(W.transpose()));
		int col = func.Fonctions.getMaxDet(wpca);		
		return wpca.getMatrix(0, wpca.getRowDimension()-1, col, col);
	}
	
	public static Matrix WFLD(Matrix W)
	{
		Matrix num = W.times(WPCA(W).times(BetweenScatterMatrix(W).times(WPCA(W).transpose().times(W.transpose()))));
		Matrix denum = W.times(WPCA(W).times(WithinScatterMatrix(W).times(WPCA(W).transpose().times(W.transpose()))));
		// Reminder X/Y = X*Y^⁻1
		Matrix wfld = num.times(denum.transpose());
		int col = func.Fonctions.getMaxDet(wfld);
		wfld = wfld.getMatrix(0, wfld.getRowDimension()-1, col, col);
		
		return wfld;
	}
	
	public static Matrix WOPT(Matrix W, Matrix sb)
	{
		Matrix wopt = WPCA(W).inverse().times(WFLD(W).inverse()).inverse();
		int col = func.Fonctions.getMaxDet(W);
		return wopt.getMatrix(0, wopt.getRowDimension()-1, col, col);
	}
}
