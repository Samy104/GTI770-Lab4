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
		long lecture = System.currentTimeMillis();
		Matrix preparedMatrix = null;
			try {
				preparedMatrix = func.Fonctions.PrepareMatrix(new File("Octave-Matrices"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		System.out.println("La lecture des images a pris au total " + (System.currentTimeMillis() - lecture) + " ms");
		
		/* Validation K-Fold */
		long valid = System.currentTimeMillis();
		Validate(preparedMatrix);
		System.out.println("La validation croisée a pris au total " + (System.currentTimeMillis() - valid) + " ms");
		System.out.println("L'application a pris au total " + (System.currentTimeMillis() - lecture) + " ms");
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
		
		long start=0;
		for(int k = 0; k < 10 ; k++)
		{
			start = System.currentTimeMillis();
			System.out.println("Debut de l'entrainement");
			EntrainerModele(func.Fonctions.aggregateExceptOne(matrixArray, k));
			System.out.println("Fin de l'entrainement en " +  (System.currentTimeMillis() - start) + "ms" );
			System.out.println("Debut du test");
			EvaluerModele(matrixArray.get(k));
			System.out.println("Fin du test en " +  (System.currentTimeMillis() - start) + "ms");
			System.out.println("");
		}
	}
	
	/**
	 * Entraîne le modèle sur la matrice passée en paramètre
	 * @param Matrice 
	 */
	
	public static void EntrainerModele(Matrix ent)
	{
		pcaEntraine = new PCA(ent);
		pcaEntraine.calculate();
		Matrix toTrainProjected = pcaEntraine.getProjectedMatrix();
		woptEntraine = func.Fisherfaces.WOPT(toTrainProjected);
	}
	
	/**
	 * La matrice passée en paramètre sera évaluée.
	 * @param ev
	 */
	
	public static void EvaluerModele(Matrix ev)
	{
		int nombreErreur = 0;
		
		Matrix vecteurPoidsEntraine = woptEntraine.times(pcaEntraine.getProjectedMatrix());
		for(int col = 0; col < ev.getColumnDimension(); col++)
		{
			Matrix currentImageCentre = func.Fonctions.CalculateCenteredMatrix(ev.getMatrix(0, ev.getRowDimension()-1, col, col));
			Matrix vecteurPoidsTest = woptEntraine
					.times(pcaEntraine.getWk()
					.times(currentImageCentre
							));
			int best = 0;
			double total = 99999;
			for(int colE = 0; colE < vecteurPoidsEntraine.getColumnDimension(); colE++)
			{
				double tempTot = 0;
				for(int row = 0; row < vecteurPoidsEntraine.getRowDimension(); row++)
				{
					tempTot += (vecteurPoidsEntraine.get(row, colE) 
							+vecteurPoidsTest.get(row, 0))/2;
				}
				best = (Math.abs(tempTot) < Math.abs(total)) ? colE : best;
				total = (Math.abs(tempTot) < Math.abs(total)) ? tempTot : total;
			}
			
			if(best == col)
			{
				nombreErreur++;
			}
			
		}
		double prob = (double)nombreErreur/(double)ev.getColumnDimension();
		System.out.println("En effectuant ce test nous avons " + nombreErreur+ " erreur(s)");
		System.out.println("La probabilité d'erreur dans ce test est de " +prob);
	}
}
