import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

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
	Double[][] OrderedMatrices;
	

	public PCA (Matrix main)
	{
		this.xMatrix = main;
	}
	
	public void Calculate()
	{
		
		this.xBar = func.Fonctions.CalculateCenteredMatrix(xMatrix);
		/*this.matriceDeCovariance = xBar;
		*/
		this.matriceDeCovariance = xMatrix.times(xMatrix.transpose());
		/*Matrix D = matriceDeCovariance.eig().getD();
		Matrix VecteursV = xMatrix.times(D);*/
		
		//Matrix S = xMatrix.svd().getS();
		//Matrix V = xMatrix.svd().getV();
		//Matrix U = xMatrix.svd().getU();
		
		Matrix V2 = vecteurPropre(xMatrix);
		func.Fonctions.printMatrix(V2, "Output/XbarCentered.txt");
		
		//func.Fonctions.printMatrix(S, "Output/S.txt");
		//func.Fonctions.printMatrix(V, "Output/V.txt");
		//func.Fonctions.printMatrix(U, "Output/U.txt");
		
		
		//Matrix vecteurPropre = xMatrix.times(vecteurPropre(xMatrix));
		Matrix swapped = getSwappedDIAGMatrix(diagonal(xMatrix));
		//swapped.print(2, 6);
		calculatePrincipauxVec(swapped,xMatrix);
		
		func.Fonctions.printMatrix(xMatrix.times(swapped),"Output/Testing.txt");
		
		
		Matrix Wk = principauxVecteurs.times(getXbar().transpose());
		this.Z = Wk.times(getXbar());
		System.out.println(this.Z.getRowDimension() + " " +this.Z.getColumnDimension());
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
		
		
		System.out.println("Number of principaux vectors is " + i);
		Matrix vecProp = vecteurPropre(matriceDeCov);
		// principauxVecteurs = vecProp.getMatrix(0, vecProp.getRowDimension()-1, vecProp.getColumnDimension()-1-i, vecProp.getColumnDimension()-1);
		principauxVecteurs = getFirstIPrincVec(vecProp, i);
	
	//System.out.println("Vecteurs propres choisi: ");
	//principauxVecteurs.print(i, 8);
		
	}
	

	
	private Matrix getFirstIPrincVec(Matrix propre, int i) {
		// TODO Auto-generated method stub
		
		Matrix vecTot = new Matrix(i,propre.getColumnDimension());
		
		for(int currentPixel = 0; currentPixel < i; currentPixel++)
		{
			// Track current i from OrderedMatrices then get position it is at now and add it to the Matrix
			int currentPrincipaux = OrderedMatrices[i][1].intValue()-1;
			
			vecTot.setMatrix(currentPixel, currentPixel, 0, propre.getColumnDimension()-1, propre.getMatrix(OrderedMatrices[currentPrincipaux][1].intValue()-1, OrderedMatrices[currentPrincipaux][1].intValue()-1, 0, propre.getColumnDimension()-1));
		}
		
		return vecTot;
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
		
		OrderedMatrices = new Double[start.getRowDimension()][2];
		
		for(int i = 0; i < start.getRowDimension(); i++)
		{
			OrderedMatrices[i][0] = Math.abs(start.get(i,i));
			OrderedMatrices[i][1] = (double) i;
		}
		Arrays.sort(OrderedMatrices, 
				new Comparator<Double[]>() {
			        @Override
			        public int compare(Double[] o1, Double[] o2) {
			            return o2[0].compareTo(o1[0]);
			        }
			    });
		
		for(int i = 0; i < start.getRowDimension(); i++)
		{
			exit.set(i, i, OrderedMatrices[i][0]);
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
		//System.out.println("Pourcentage de alpha: " + (num/denum));
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
