import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import Jama.SingularValueDecomposition;

public class PCA {

	Matrix xMatrix = null;
	
	public Matrix xBar = null;
	public Matrix R = null;
	
	public Matrix matriceDeCovariance = null;
	
	Matrix principauxVecteurs = null;
	Matrix Z = null;

	

	public PCA (Matrix main)
	{
		this.xMatrix = main;
	}
	
	public void Calculate()
	{
		
		/*this.xBar = func.Fonctions.GenerateScatterMatrix(xMatrix);
		this.matriceDeCovariance = xBar;
		*/
		this.matriceDeCovariance = (xMatrix.transpose()).times(xMatrix);
		/*Matrix D = matriceDeCovariance.eig().getD();
		Matrix VecteursV = xMatrix.times(D);*/
		
		//Matrix S = xMatrix.svd().getS();
		//Matrix V = xMatrix.svd().getV();
		//Matrix U = xMatrix.svd().getU();
		
		Matrix V2 = vecteurPropre(xMatrix);
		func.Fonctions.printMatrix(xMatrix, "Output/XbarCentered.txt");
		
		//func.Fonctions.printMatrix(S, "Output/S.txt");
		//func.Fonctions.printMatrix(V, "Output/V.txt");
		//func.Fonctions.printMatrix(U, "Output/U.txt");
		
		
		//Matrix vecteurPropre = xMatrix.times(vecteurPropre(xMatrix));
		Matrix swapped = getSwappedDIAGMatrix(diagonal(xMatrix));
		
		calculatePrincipauxVec(swapped,xMatrix);
		
		func.Fonctions.printMatrix(xMatrix.times(swapped),"Output/Testing.txt");
		
		System.out.println(matriceDeCovariance.getRowDimension() + " " +matriceDeCovariance.getColumnDimension());
		
		this.Z = xMatrix.times(swapped);
		//Matrix W = matriceDeCovariance.times(vecteurPropre);
		//func.Fonctions.printMatrix(W, "Output/WMatrix.txt");
		//printToFile("Output/datMatrix.txt");
		
	}
	
	public void calculatePrincipauxVec(Matrix swap,Matrix matriceDeCov){
		int i = 0;
		
		while(i < swap.getRowDimension() && !getKPrincipauxVecteurs(i, swap))	{
			i++;			
		}
		i++;
		
		principauxVecteurs = vecteurPropre(matriceDeCov).getMatrix(0, vecteurPropre(matriceDeCov).getRowDimension()-1,
				vecteurPropre(matriceDeCov).getColumnDimension()-i, vecteurPropre(matriceDeCov).getColumnDimension()-1);
	
	//System.out.println("Vecteurs propres choisi: ");
	//principauxVecteurs.print(i, 8);
		
	}
	

	
	public Matrix vecteurPropre(Matrix matriceDeCovariance){		
		return matriceDeCovariance.eig().getV();	
	}
	
	public Matrix diagonal(Matrix matriceDeCovariance){
		return matriceDeCovariance.eig().getD();
	}
	
	public Matrix vecPTranspose(Matrix matriceDeCovariance){
		return vecteurPropre(matriceDeCovariance).transpose();
	}
	
	public Matrix getXbar(){
		return this.xBar;
	}
	
	public Matrix getXbarTranspose(){
		return this.xBar.transpose();
	}
	
	
	public Matrix getSwappedDIAGMatrix(Matrix start)
	{
		Matrix exit = new Matrix(start.getRowDimension(),start.getColumnDimension());
		
		Double[] sort = new Double[start.getRowDimension()];
		
		for(int i = 0; i < start.getRowDimension(); i++)
		{
			sort[i] = start.get(i,i);
		}
		Arrays.sort(sort, Collections.reverseOrder());
		
		for(int i = 0; i < start.getRowDimension(); i++)
		{
			exit.set(i, i, sort[i]);
		}
		
		return exit;
	}
	
	public boolean getKPrincipauxVecteurs(int k, Matrix from)
	{
		boolean decision = false;
		
		double num = 0;
		double denum = 0;
		
		for(int i = 0; i < from.getRowDimension(); i++)
		{
			if(i <= k)
			{
				num += from.get(i, i);
			}
			denum += from.get(i, i);
		}
		System.out.println("Pourcentage de alpha: " + (num/denum));
		decision = (num/denum >= 0.9) ? true : false;
		
		return decision;
	}
	
	public Matrix getSwapMatrix(Matrix swapping){
		Matrix t = new Matrix(swapping.getRowDimension(),swapping.getColumnDimension());
		
		int indexJ = 0;
		
		for (int j = swapping.getColumnDimension()-1; j >= 0;j--){	
			for(int i = 0; i < swapping.getRowDimension();i++){
				t.set(i, indexJ,
						swapping.get(i,j));
			}
			indexJ++;
		}
		return t;
	}
	
	public void printToFile(String path)
	{
		PrintWriter write = null;
		
		try
		{
			write = new PrintWriter(path, "UTF-8");
			
			write.append("XBAR CENTREE");
			for(int i = 0; i < xBar.getRowDimension(); i++){
				write.println();
				for(int j = 0; j<xBar.getColumnDimension(); j++){
					write.print(xBar.get(i, j) + " ");
				}
			}
			
			Matrix swapped = getSwappedDIAGMatrix(diagonal(matriceDeCovariance));
			
			calculatePrincipauxVec(swapped,matriceDeCovariance);
			
			
			write.append("matrice de cov");
			//Matrice de cov
			for(int i = 0; i < matriceDeCovariance.getRowDimension(); i++){
				write.println();
				for(int j = 0; j<matriceDeCovariance.getColumnDimension(); j++){
					write.print(matriceDeCovariance.get(i, j) + " ");
				}
			}
			
			//Vecteur Propre
			
			for(int i = 0; i < vecteurPropre(matriceDeCovariance).getRowDimension(); i++){
				write.println();
				for(int j = 0; j < vecteurPropre(matriceDeCovariance).getColumnDimension(); j++){
					write.print(vecteurPropre(matriceDeCovariance).get(i, j) + " ");
				}
			}
			
			//VP Transpose
			
			for(int i = 0; i < vecPTranspose(matriceDeCovariance).getRowDimension(); i++){
				write.println();
				for(int j = 0; j<vecPTranspose(matriceDeCovariance).getColumnDimension(); j++){
					write.print(vecPTranspose(matriceDeCovariance).get(i, j) + " ");
				}
			}
			
			//Diagonal
			
			for(int i = 0; i < diagonal(matriceDeCovariance).getRowDimension(); i++){
				write.println();
				for(int j = 0; j<diagonal(matriceDeCovariance).getColumnDimension(); j++){
					write.print(diagonal(matriceDeCovariance).get(i, j) + " ");
				}
			}
			
			//Z
			
			for(int i = 0; i < getXbar().times(principauxVecteurs).getRowDimension(); i++){
				write.println();
				for(int j = 0; j<getXbar().times(principauxVecteurs).getColumnDimension(); j++){
					write.print(getXbar().times(principauxVecteurs).get(i, j) + " ");
				}
			}
			
			// Do the shit for task 1-5: Projeter dans le sous espace de K Z= Xmoyenne*Vk
			
			//System.out.println("Matrice Z Projete");
			
			Z = getXbar().times(principauxVecteurs);
			
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}finally{
			write.close();
		}
		
	}
	
	public Matrix getProjectedMatrix(){
		return this.Z;
	}
}
