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
			EntrainerModele(func.Fonctions.aggregateExceptOne(matrixArray, k));
			EvaluerModele(matrixArray.get(k));
		}
	}
	
	public static void EntrainerModele(Matrix ent)
	{
		
	}
	
	public static void EvaluerModele(Matrix ev)
	{
		
	}
}
