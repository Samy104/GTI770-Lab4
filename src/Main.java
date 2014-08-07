import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import Jama.Matrix;


/**
 * @author Christopher Larivière, Samy Lemcelli
 * 
 * On doit prendre les images, les transformer et les inserrer dans une matrice
 * En harmonie, on doit créer une matrice R contenant les classes de chacune des donnes
 * On devra séparer notre matrice en 10(K) blocs de 40(Classes) et entrainer/évaluer en utilisant ces matrices.
 * On pourra par la suite imprimer les erreurs, etc.	 * 
 */

public class Main {
		
	private static Matrix woptEntraine;
	private static PCA pcaEntraine;
	
	/**
	 * Prépare la matrice et l'envoi directement à notre validation.
	 * Le point d'entrée de notre programme
	 */
	
	public static void main(String[] args) {
		Matrix preparedMatrix = null;
			try {
				preparedMatrix = func.Fonctions.PrepareMatrix(new File("Octave-Matrices"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		/* Validation K-Fold */
		Validate(preparedMatrix);
	}
	
	
	/**
	 * Fonction débutant la validation
	 * @param La matrice qui a été prépare et prète à subir un PCA / LDA.
	 */
	
	public static void Validate(Matrix preparedMatrix)
	{
		ArrayList<Matrix> matrixArray = new ArrayList<Matrix>();
		for(int k = 0; k < 10; k++)
		{
			// Sépare les matrices en 40. Chacun contenant 1 vecteur de chaque classe.
			matrixArray.add(k,func.Fonctions.AppendRecursively(preparedMatrix, k, 40));
		}
		
		double mean = 0;
		double variance = 0;
		for(int k = 0; k < 10 ; k++)
		{
			
			// Fait appel à l'entraînement et à l'évaluation du modèle
			System.out.println("Debut de l'entrainement");
			EntrainerModele(func.Fonctions.aggregateExceptOne(matrixArray, k));
			System.out.println("Fin de l'entrainement");
			System.out.println("Debut du test");
			EvaluerModele(matrixArray.get(k));
			System.out.println("Fin du test");
		}
	}
	
	/**
	 * Entraîne le modèle sur la matrice passée en paramètre
	 * @param Matrice 
	 */
	
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
		woptEntraine = func.Fisherfaces.WOPT(toTrainProjected);
		System.out.println("WOPT rows: " + woptEntraine.getRowDimension() + " cols: " + woptEntraine.getColumnDimension());
	}
	
	/**
	 * La matrice passée en paramètre sera évaluée.
	 * @param ev
	 */
	
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
