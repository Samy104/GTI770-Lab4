import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

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
		Matrix preparedMatrix = null;
			try {
				preparedMatrix = func.Fonctions.PrepareMatrix(new File("Octave-Matrices"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			
		//System.out.println("Rows: "+preparedMatrix.getRowDimension()+" Columns: " + preparedMatrix.getColumnDimension());
		
		//Print Matrix out to a file
		
		PrintWriter printer = null;
		try{
			printer = new PrintWriter("Output/fileTest.txt");
			for(int i = 0; i<preparedMatrix.getRowDimension(); i++){
				printer.println();
				for(int j = 0; j<preparedMatrix.getColumnDimension(); j++){
					printer.print(preparedMatrix.get(i, j) + " ");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Matrix xBar = func.Fonctions.CalculateCenteredMatrix(preparedMatrix);
		func.Fonctions.printMatrix(xBar,"Output/averagesImages.txt");
		
		PCA pcaTest = new PCA(xBar);
		pcaTest.Calculate();
		
		//Matrix wBet = func.Fisherfaces.BetweenScatterMatrix(preparedMatrix);
		//Matrix wOut = func.Fisherfaces.WithinScatterMatrix(preparedMatrix);
		//System.out.println(wBet.get(0,0));
		//System.out.println(wOut.get(0, 0));
		//Matrix wPca = func.Fisherfaces.WFLD(preparedMatrix);
		//System.out.println(wPca.getColumnDimension()+"  "+wPca.getRowDimension());
		//wPca.print(10,2);
		// Cross Validation
		
		
		//CheckIfWorks(preparedMatrix);
		//Validate(preparedMatrix);
		//CheckIfWorks(preparedMatrix);
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
		// With the given matrix to train calculate the new PCA
		PCA toTrain = new PCA(ent);
		toTrain.Calculate();
		Matrix toTrainProjected = toTrain.getProjectedMatrix();
		
		// Having the new PCA get the Fisher
		// We need to store the return to a variable for EvaluerModele
		func.Fisherfaces.WPCA(toTrainProjected);
		
		
	}
	
	public static void EvaluerModele(Matrix ev)
	{
		
	}
}
