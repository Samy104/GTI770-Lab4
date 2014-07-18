import java.io.File;
import java.util.ArrayList;

import Jama.Matrix;


public class Main {
	
	/*
	 * On doit prendre les images, les transformer et les inserrer dans une matrice
	 * En harmonie, on doit créer une matrice R contenant les classes de chacune des donnes
	 * On devra séparer notre matrice en 10(K) blocs de 40(Classes) et entrainer/évaluer en utilisant ces matrices.
	 * On pourra par la suite imprimer les erreurs, etc.
	 * 
	 */
	
	public void main(String[] args) {
		
		Matrix preparedMatrix = func.Fonctions.PrepareMatrix(new File("../GTI770-L4/Faces-dataset"));
		
		
		// Cross Validation
		Validate(preparedMatrix);
	}

	public void Validate(Matrix preparedMatrix)
	{
		ArrayList<Matrix> matrixArray = new ArrayList<Matrix>();
		// Prepare Matrices
		for(int k = 0; k < 10; k++)
		{
			// Split Matrices into 40 Matrices containing 1 vector of each class
			//matrixArray.set(0,preparedMatrix.getMatrix(0, 1, arg2, arg3));
		}
		
		//Validate
		double mean = 0;
		double variance = 0;
		for(int k = 0; k < 10 ; k++)
		{
			
		}
	}
}
