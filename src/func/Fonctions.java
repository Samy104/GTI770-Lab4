package func;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.*;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;

import Jama.Matrix;
/*
 * Functions for Matrices
 */
public class Fonctions {
	public static int numberClasses = 0;
	
	private Fonctions(){}
		
	public static Matrix MatrixToColumnMatrix(Matrix orig)
	{
		Matrix newMat = new Matrix(orig.getColumnDimension()*orig.getRowDimension(),1);
		
		for(int col = 0; col < orig.getColumnDimension(); col++)
		{
			newMat.setMatrix(0+col*orig.getRowDimension(), orig.getRowDimension()-1+col*orig.getRowDimension(), 0, 0, orig.getMatrix(0, orig.getRowDimension()-1, col, col));
		}
		
		return newMat;
	}
	
	public static void createImage(String path, FileInputStream fis){
		
	}
	
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

	public static int getMaxDet(Matrix x)
	{
		double max = 0;
		int index = 0;
		
		for(int col = 0; col < x.getColumnDimension(); col++)
		{
			//System.out.println(x.getRowDimension() + " " + x.getColumnDimension());
			double det = x.getMatrix(0, x.getRowDimension()-1, col, col).det();
			index = (det > max) ? col : index;
			max = (det > max) ? det : max;
		}
		
		return index;
	}
	
	
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
	public static double mean(Matrix x, int column)
	{
		double data = 0;
		for(int i = 0; i < x.getRowDimension(); i++)
		{
			data += x.get(i, column);
		}
		
		return data/x.getRowDimension();
	}
	
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
	
	public static int GetAmount(Matrix matrix, int i) {
		int count = 0;
		for(int row = 0; row < matrix.getRowDimension(); row++)
		{
			count += (matrix.get(row, 0) == i) ? 1 : 0;
		}
		
		return count;
	}

	public static Matrix GetMatrixPowered(Matrix m, int power)
	{
		for(int i = 0; i <= power; i++)
		{
			m.times(m);
		}
		
		return m;
	}
	
	public static Matrix GetEPowered(Matrix m)
	{
		Matrix e = new Matrix(m.getRowDimension(),m.getColumnDimension());
		
		for(int i = 0; i < e.getRowDimension();i++)
		{
			for(int j = 0; j < e.getColumnDimension(); j++)
			{
				e.set(i, j, Math.pow(Math.E,m.get(i, j)));
			}
		}
		
		return e;
	}
	public static Matrix GetLogTen(Matrix m)
	{
		Matrix l = new Matrix(m.getRowDimension(),m.getColumnDimension());
		
		for(int i = 0; i < l.getRowDimension();i++)
		{
			for(int j = 0; j < l.getColumnDimension(); j++)
			{
				l.set(i, j, Math.log10(m.get(i, j)));
			}
		}
		
		return l;
	}
	
	public static Matrix AddToMatrix(Matrix m, double d)
	{
		Matrix a = new Matrix(m.getRowDimension(),m.getColumnDimension());
		
		for(int i = 0; i < a.getRowDimension();i++)
		{
			for(int j = 0; j < a.getColumnDimension(); j++)
			{
				a.set(i, j, (m.get(i, j) + d));
			}
		}
		
		return a;
	}
	
	public static Matrix AddToMatrix(Matrix m, int x)
	{
		Matrix a = new Matrix(m.getRowDimension(),m.getColumnDimension());
		
		for(int i = 0; i < a.getRowDimension();i++)
		{
			for(int j = 0; j < a.getColumnDimension(); j++)
			{
				a.set(i, j, (m.get(i, j) + x));
			}
		}
		
		return a;
	}
	
	public static Matrix RemoveToMatrix(Matrix m, int x)
	{
		Matrix a = new Matrix(m.getRowDimension(),m.getColumnDimension());
		
		for(int i = 0; i < a.getRowDimension();i++)
		{
			for(int j = 0; j < a.getColumnDimension(); j++)
			{
				a.set(i, j, (m.get(i, j) - x));
			}
		}
		
		return a;
	}
	
	public static Matrix RemoveToMatrix(Matrix m, double x)
	{
		Matrix a = new Matrix(m.getRowDimension(),m.getColumnDimension());
		
		for(int i = 0; i < a.getRowDimension();i++)
		{
			for(int j = 0; j < a.getColumnDimension(); j++)
			{
				a.set(i, j, (m.get(i, j) - x));
			}
		}
		
		return a;
	}
	
	public static Matrix DivideToMatrix(Matrix m, int x)
	{
		Matrix a = new Matrix(m.getRowDimension(),m.getColumnDimension());
		
		for(int i = 0; i < a.getRowDimension();i++)
		{
			for(int j = 0; j < a.getColumnDimension(); j++)
			{
				a.set(i, j, (m.get(i, j) / x));
			}
		}
		
		return a;
	}
	
	public static Matrix DivideToMatrix(Matrix m, double x)
	{
		Matrix a = new Matrix(m.getRowDimension(),m.getColumnDimension());
		
		for(int i = 0; i < a.getRowDimension();i++)
		{
			for(int j = 0; j < a.getColumnDimension(); j++)
			{
				a.set(i, j, (m.get(i, j) / x));
			}
		}
		
		return a;
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
	 * La fonction prends le ArrayList de Matrices et choisis les 225 rangées qui n'appartiennent pas à l'indice.
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
		
		// Once the nbClass == 0 this will be the final return
		return rec;
	}
	
	
	public static Matrix AppendMatrix(Matrix a, Matrix b){
		Matrix augmentedMatrix = new Matrix(a.getRowDimension(),a.getColumnDimension()+b.getColumnDimension());
		
		augmentedMatrix.setMatrix(0, a.getRowDimension()-1, 0, a.getColumnDimension()-1, a);
		augmentedMatrix.setMatrix(0, a.getRowDimension()-1, a.getColumnDimension(), a.getColumnDimension()+b.getColumnDimension()-1, b);
		
		return augmentedMatrix;
	}
	


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
