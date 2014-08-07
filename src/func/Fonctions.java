package func;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;
import java.util.Random;

import Jama.Matrix;
/**
 * @author Samy Lemcelli, Christopher Larivière
 * Functions for Matrices
 */
public class Fonctions {
	
	public static int numberClasses = 0;
	
	private Fonctions(){}
		
	
	/**
	 * Debute la fonction, converti chaque image en une matrice colonne à l'aide des fonctions AppendMatrix et AppendMatrixRecursively
	 * @param Files les dossiers 
	 * @return Matrix une matrice préparé  
	 * @throws IOException (si le fichier ne peut pas être lu.
	 */
	
	public static Matrix PrepareMatrix(File files) throws IOException{
		Matrix collectionOfFiles = null;
		File[] sup = files.listFiles();
		Arrays.sort(sup);
		
		for(File file : sup)
		{
			if(file.isDirectory())
			{
				System.out.println("Directory: " + file.getName());
				//PrepareMatrix(file);
				if(collectionOfFiles == null)
				{
					collectionOfFiles = PrepareMatrix(file); // Calls same method again.
				}
				else
				{
					collectionOfFiles = AppendMatrix(collectionOfFiles, PrepareMatrix(file));
				}
				numberClasses++;
			}
			else 
			{
				//System.out.println("File to matrix: " + file.getName());
				Matrix matrixToAdd = GetMatrixFromFile(file.getAbsolutePath(),92,112);
				
				
				if(collectionOfFiles == null){
					collectionOfFiles = matrixToAdd;
				}
				else{
					collectionOfFiles = AppendMatrix(collectionOfFiles, matrixToAdd);
				}
			}
		}
		return collectionOfFiles;
	}
	
	/**
	 * 
	 * @param Le chemin du fichier à lire.
	 * @param la largeur de l'image. Pour ce laboratoire tout les images sont de largeur 92 
	 * @param l'hauteur de l'image. Pour ce laboratoire tout les images sont de hauteur 112
	 * @return Matrice de taille 10304 x 1
	 */
	
	@SuppressWarnings("resource")
	private static Matrix GetMatrixFromFile(String fileString, int picWidth, int picHeight) {
	
			Matrix matrix = new Matrix(picWidth*picHeight,1);
		    try 
		    {
		    	Scanner scanner = new Scanner(new File(fileString));

		        for(int i = 0; i < matrix.getRowDimension()-1; i++) 
		        {
		        	int valeur = scanner.nextInt();
		            matrix.set(i, 0, valeur);
		        }
		    } 
		    catch(Exception ex)
		    {
		    	ex.printStackTrace();
		    }
			return matrix;
	}	
	
	/**
	 * Calcule la matrice centré de la matrice passée en paramètre
	 * @param Matrice à calculer sont centrées
	 * @return
	 */
	
	public static Matrix CalculateCenteredMatrix(Matrix x){	
		
		Matrix xBar = new Matrix(x.getRowDimension(), x.getColumnDimension());
		double average;
		for(int j = 0; j < x.getColumnDimension();j++){
			average = mean(x,j);
			for(int i = 0; i < x.getRowDimension();i++){
				double meanTest = (x.get(i, j) - average);
				xBar.set(i, j,meanTest);	
			}
		}
		
		return (xBar);
	}
	
	/**
	 * Génère la matrice dispersée St
	 * @param Matrice à dispersée
	 * @return Matrice dispersée
	 */
	
	public static Matrix GenerateScatterMatrix(Matrix x){	
		
		Matrix xBar = new Matrix(x.getRowDimension(), x.getColumnDimension());
		double average;
		for(int j = 0; j < x.getColumnDimension();j++){
			average = mean(x,j);
			for(int i = 0; i < x.getRowDimension();i++){
				double meanTest = (x.get(i, j) - average);
				xBar.set(i, j,meanTest);	
			}
		}
		
		return (xBar.transpose().times(xBar));
	}
	
	/**
	 * Calcule la moyenne pour la colonne de la matrice passée en paramètre
	 * @param Matrix à calculer la moyenne
	 * @param Le numéro de la colonne que vous voulez calculer 
	 * @return moyenne de la colonne
	 */
	
	public static double mean(Matrix x, int column)
	{
		double data = 0;
		for(int i = 0; i < x.getRowDimension(); i++)
		{
			data += x.get(i, column);
		}
		
		return data/x.getRowDimension();
	}
	
	/**
	 * Calcule la moyenne total de la matrice passée en paramètre
	 * @param Matrice x
	 * @return la moyenne total de la matrice
	 */
	
	public static double meanTotal(Matrix x)
	{
		double[] data = new double[x.getColumnDimension()];
		double total = 0;
		for(int j = 0; j < x.getColumnDimension(); j++)
		{
			for(int i = 0; i < x.getRowDimension(); i++)
			{
				data[j] += x.get(i, j);
			}
			data[j] = data[j]/x.getRowDimension();
		}
		
		for(int count = 0; count < data.length; count++)
		{
			total += data[count];
		}
		
		return total/data.length;
	}
	
	/**
	 * Retourne une matrice aléatoirement choisi
	 * @author Samy Lemcelli
	 * 
	 * @param Matrice m
	 * @return Matrice aléatoire
	 */
	
	public static Matrix returnRandomizedMatrix(Matrix m) {
		
		Matrix randMatrix = new Matrix(m.getRowDimension(), m.getColumnDimension());
		ArrayList<Integer> remainingElements = new ArrayList<Integer>(m.getRowDimension());
		
		for(int i = 0; i < m.getRowDimension(); i++)
		{
			remainingElements.add(i);
		}
		int finalPosition = 0;
		while(!remainingElements.isEmpty())
		{
			int position = new Random().nextInt(remainingElements.size());
			int random = remainingElements.get(position);
			randMatrix.set(finalPosition,0,m.get(random, 0));
			randMatrix.set(finalPosition,1,m.get(random, 1));
			remainingElements.remove(position);
			finalPosition++;
		}
		return randMatrix;
	}
	
	/**
	 * La fonction prends une ArrayList de Matrices et choisis les 225 rangées qui n'appartiennent pas à l'indice.
	 * 
	 * @param ArrayList<Matrix> agg
	 * @param int thene
	 * @return Matrice aggregation
	 */
	
	public static Matrix aggregateExceptOne(ArrayList<Matrix> agg, int theone)
	{
		Matrix aggregation = new Matrix(agg.get(0).getRowDimension(),(agg.size()-1)*func.Fonctions.numberClasses);
		int currentPos = 0;
		
		for(int i = 1; i < 10; i++)
		{
			if(theone != i)
			{
				aggregation.setMatrix(currentPos,(currentPos+func.Fonctions.numberClasses-1), 0, func.Fonctions.numberClasses-1, agg.get(i));
				currentPos += func.Fonctions.numberClasses;
			}
			
		}
		
		return aggregation;
	}
	
	
	/**
	 * Ajoute récursivement les nouvelles colonnes d'images dans la matrice passée en paramètre
	 * @param matrice passée en paramètre
	 * @param constante k
	 * @param nombre de classes restantes
	 * @return
	 */
	public static Matrix AppendRecursively(Matrix initial, int k, int nbClass)
	{
		Matrix rec = null;
		if(nbClass > 1)
		{
			rec = AppendMatrix(initial.getMatrix(0, initial.getRowDimension()-1, (nbClass-1)*10+k, (nbClass-1)*10+k),AppendRecursively(initial,k, nbClass-1));
		}
		else
		{
			rec = initial.getMatrix(0, initial.getRowDimension()-1, (nbClass-1)*10+k, (nbClass-1)*10+k);
		}
		
		//Lorsque nbClass == 0 ceci serait le dernier retour
		return rec;
	}
	
	/**
	 * Concatène la matrice B à la matrice A
	 * @param Matrice A
	 * @param Matrice image x par 1
	 * @return Matrice A avec le B dedans.
	 */
	
	public static Matrix AppendMatrix(Matrix a, Matrix b){
		Matrix augmentedMatrix = new Matrix(a.getRowDimension(),a.getColumnDimension()+b.getColumnDimension());
		
		augmentedMatrix.setMatrix(0, a.getRowDimension()-1, 0, a.getColumnDimension()-1, a);
		augmentedMatrix.setMatrix(0, a.getRowDimension()-1, a.getColumnDimension(), a.getColumnDimension()+b.getColumnDimension()-1, b);
		
		return augmentedMatrix;
	}
	
	/**
	 * Imprime la matrice au fichier que nous spécifions
	 * Ex: printMatrix(matrice à imprimer,"Output/matriceàimprimer.txt");
	 * @param la matrice à imprimer
	 * @param le chemin ou vous voulez sauvegarder votre matrice imprimer.
	 */

	public static void printMatrix(Matrix theMatrix,String path){		
		PrintWriter printer = null;
		try{
			printer = new PrintWriter(path);
			for(int i = 0; i<theMatrix.getRowDimension(); i++){
				printer.println();
				for(int j = 0; j<theMatrix.getColumnDimension(); j++){
					printer.print(theMatrix.get(i, j) + " ");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}
