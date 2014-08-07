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
	
	private static Matrix woptEntraine;
	private static PCA pcaEntraine;
	
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
		
		/*PrintWriter printer = null;
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
		*/
		//Matrix wBet = func.Fisherfaces.BetweenScatterMatrix(preparedMatrix);
		//Matrix wOut = func.Fisherfaces.WithinScatterMatrix(preparedMatrix);
		//System.out.println(wBet.get(0,0));
		//System.out.println(wOut.get(0, 0));
		//Matrix wPca = func.Fisherfaces.WFLD(preparedMatrix);
		//System.out.println(wPca.getColumnDimension()+"  "+wPca.getRowDimension());
		//wPca.print(10,2);
		// Cross Validation
		
		
		//CheckIfWorks(preparedMatrix);
		Validate(preparedMatrix);
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
			System.out.println("Debut de l'entrainement");
			EntrainerModele(func.Fonctions.aggregateExceptOne(matrixArray, k));
			System.out.println("Fin de l'entrainement");
			System.out.println("Debut du test");
			EvaluerModele(matrixArray.get(k));
			System.out.println("Fin du test");
		}
	}
	
	public static void EntrainerModele(Matrix ent)
	{
		System.out.println(ent.getRowDimension() + "  :   " + ent.getColumnDimension());
		// With the given matrix to train calculate the new PCA
		pcaEntraine = new PCA(ent);
		pcaEntraine.Calculate();
		Matrix toTrainProjected = pcaEntraine.getProjectedMatrix();
		func.Fonctions.printMatrix(toTrainProjected,"Output/reduite.txt");
		//toTrainProjected.print(2, 2);
		// Having the new PCA get the Fisher
		// We need to store the return to a variable for EvaluerModele
		woptEntraine = func.Fisherfaces.WOPT(toTrainProjected, true);
		System.out.println("WOPT rows: " + woptEntraine.getRowDimension() + " cols: " + woptEntraine.getColumnDimension());
	}
	
	public static void EvaluerModele(Matrix ev)
	{
		// Reduce the dimensions to fit with the old
		for(int col = 0; col < ev.getColumnDimension(); col++)
		{
			Matrix currentImageCentre = func.Fonctions.CalculateCenteredMatrix(ev.getMatrix(0, ev.getRowDimension()-1, col, col));
			System.out.println("W:" + woptEntraine.getRowDimension() + "  " + woptEntraine.getColumnDimension());
			System.out.println("PCA:" + pcaEntraine.getPrincipauxVecteurs().getRowDimension() + "  " + pcaEntraine.getPrincipauxVecteurs().getColumnDimension());
			System.out.println("Xbar:" + currentImageCentre.getRowDimension() + "  " + currentImageCentre.getColumnDimension());
			Matrix vecteurPoids = woptEntraine
					.times(pcaEntraine.getPrincipauxVecteurs()
					.times(currentImageCentre
							));
			vecteurPoids.print(2, 2);
			
		}
		
		
		
	}
}
