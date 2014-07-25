import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Jama.Matrix;


public class Main {
	
	/*
	 * On doit prendre les images, les transformer et les inserrer dans une matrice
	 * En harmonie, on doit créer une matrice R contenant les classes de chacune des donnes
	 * On devra séparer notre matrice en 10(K) blocs de 40(Classes) et entrainer/évaluer en utilisant ces matrices.
	 * On pourra par la suite imprimer les erreurs, etc.
	 * 
	 */
	
	public static void main(String[] args) {
		
		Matrix preparedMatrix = func.Fonctions.PrepareMatrix(new File("../gti770-lab4/Faces-dataset"));
		
		//Matrix wBet = func.Fisherfaces.BetweenScatterMatrix(preparedMatrix);
		//Matrix wOut = func.Fisherfaces.WithinScatterMatrix(preparedMatrix);
		//System.out.println(wBet.get(0,0));
		//System.out.println(wOut.get(0, 0));
		//Matrix wPca = func.Fisherfaces.WFLD(preparedMatrix);
		//System.out.println(wPca.getColumnDimension()+"  "+wPca.getRowDimension());
		//wPca.print(10,2);
		// Cross Validation
		Validate(preparedMatrix);
	}

	public static void Validate(Matrix preparedMatrix)
	{
		
		ArrayList<Matrix> matrixArray = new ArrayList<Matrix>();
		// Prepare Matrices
		for(int k = 0; k < 10; k++)
		{
			// Split Matrices into 40 Matrices containing 1 vector of each class
			matrixArray.add(k,func.Fonctions.AppendRecursively(preparedMatrix, k, 40));
		}
		
		//Validate
		double mean = 0;
		double variance = 0;
		for(int k = 0; k < 10 ; k++)
		{
			//System.out.println("Mean total for k interation " + k + " " +func.Fonctions.meanTotal(func.Fonctions.aggregateExceptOne(matrixArray, k)));
			EntrainerModele(func.Fonctions.aggregateExceptOne(matrixArray, k));
			EvaluerModele(matrixArray.get(k));
		}
	}
	
	public static void EntrainerModele(Matrix ent)
	{
		PCA z = new PCA(ent);
		z.Calculate();
		Matrix eigen = func.Fisherfaces.computeEigen(z.Z);
		//eigen.print(1, 0);
		//func.Fisherfaces.WPCA(eigen);
	}
	
	public static void EvaluerModele(Matrix ev)
	{
		
	}
}
