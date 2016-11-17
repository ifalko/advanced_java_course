package edu.technopolis.homework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * The {@code MatrixMultiplication} class implements
 * fast multiplication 2 matrices at each other.
 * The {@code MatrixMultiplication} uses Strassen algorithm and
 * parallelize it with the {@link java.util.concurrent.ForkJoinPool}
 *
 * @author Alexey Falko
 * @author Evgeny Usov
 */
public class MatrixMultiplication {

    //******************************************************************************************

    public static long[][] multiply(long[][] a, long[][] b) {

        int rowsA = a.length;
        int columnsB = b[0].length;
        int columnsA_rowsB = a[0].length;

        long[][] c = new long[rowsA][columnsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < columnsB; j++) {
                int sum = 0;
                for (int k = 0; k < columnsA_rowsB; k++) {
                    sum += a[i][k] * b[k][j];
                }
                c[i][j] = sum;
            }
        }
        return c;
    }

    //******************************************************************************************

    public static long[][] multiplyTransposed(long[][] a, long[][] b) {

        int rowsA = a.length;
        int columnsB = b[0].length;
        int columnsA_rowsB = a[0].length;

        long columnB[] = new long[columnsA_rowsB];
        long[][] c = new long[rowsA][columnsB];


        for (int j = 0; j < columnsB; j++) {
            for (int k = 0; k < columnsA_rowsB; k++) {
                columnB[k] = b[k][j];
            }

            for (int i = 0; i < rowsA; i++) {
                int rowA[] = a[i];
                int sum = 0;
                for (int k = 0; k < columnsA_rowsB; k++) {
                    sum += rowA[k] * columnB[k];
                }
                c[i][j] = sum;
            }
        }

        return c;
    }

    //******************************************************************************************

    private static long[][] summation(long[][] a, long[][] b) {

        int n = a.length;
        int m = a[0].length;
        long[][] c = new long[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                c[i][j] = a[i][j] + b[i][j];
            }
        }
        return c;
    }

    //******************************************************************************************

    private static long[][] subtraction(long[][] a, long[][] b) {

        int n = a.length;
        int m = a[0].length;
        long[][] c = new int[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                c[i][j] = a[i][j] - b[i][j];
            }
        }
        return c;
    }

    //******************************************************************************************

    private static long[][] addition2SquareMatrix(long[][] a, int n) {

        long[][] result = new long[n][n];

        for (int i = 0; i < a.length; i++) {
            System.arraycopy(a[i], 0, result[i], 0, a[i].length);
        }
        return result;
    }

    //******************************************************************************************

    private static long[][] getSubmatrix(long[][] a, int n, int m) {
        long[][] result = new long[n][m];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, result[i], 0, m);
        }
        return result;
    }

    //******************************************************************************************

    private static void splitMatrix(long[][] a, long[][] a11, long[][] a12, long[][] a21, long[][] a22) {

        int n = a.length >> 1;

        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, a11[i], 0, n);
            System.arraycopy(a[i], n, a12[i], 0, n);
            System.arraycopy(a[i + n], 0, a21[i], 0, n);
            System.arraycopy(a[i + n], n, a22[i], 0, n);
        }
    }

    //******************************************************************************************

    private static long[][] collectMatrix(long[][] a11, long[][] a12, long[][] a21, long[][] a22) {

        int n = a11.length;
        long[][] a = new long[n << 1][n << 1];

        for (int i = 0; i < n; i++) {
            System.arraycopy(a11[i], 0, a[i], 0, n);
            System.arraycopy(a12[i], 0, a[i], n, n);
            System.arraycopy(a21[i], 0, a[i + n], 0, n);
            System.arraycopy(a22[i], 0, a[i + n], n, n);
        }

        return a;
    }

    //******************************************************************************************

    /**
     * Multi-threaded matrix multiplication
     * algorithm by Strassen
     */
    private static class myRecursiveTask extends RecursiveTask<long[][]> {
        private static final long serialVersionUID = -433764214304695286L;

        int n;
        long[][] a;
        long[][] b;

        public myRecursiveTask(long[][] a, long[][] b, int n) {
            this.a = a;
            this.b = b;
            this.n = n;
        }

        /**
         * @return the long matrix by
         * multiplying 2 matrices at each other
         */
        @Override
        protected long[][] compute() {
            if (n <= 128) {
                return multiplyTransposed(a, b);
            }

            n >>= 1;

            long[][] a11 = new long[n][n];
            long[][] a12 = new long[n][n];
            long[][] a21 = new long[n][n];
            long[][] a22 = new long[n][n];

            long[][] b11 = new long[n][n];
            long[][] b12 = new long[n][n];
            long[][] b21 = new long[n][n];
            long[][] b22 = new long[n][n];

            splitMatrix(a, a11, a12, a21, a22);
            splitMatrix(b, b11, b12, b21, b22);

            myRecursiveTask task_p1 = new myRecursiveTask(summation(a11, a22), summation(b11, b22), n);
            myRecursiveTask task_p2 = new myRecursiveTask(summation(a21, a22), b11, n);
            myRecursiveTask task_p3 = new myRecursiveTask(a11, subtraction(b12, b22), n);
            myRecursiveTask task_p4 = new myRecursiveTask(a22, subtraction(b21, b11), n);
            myRecursiveTask task_p5 = new myRecursiveTask(summation(a11, a12), b22, n);
            myRecursiveTask task_p6 = new myRecursiveTask(subtraction(a21, a11), summation(b11, b12), n);
            myRecursiveTask task_p7 = new myRecursiveTask(subtraction(a12, a22), summation(b21, b22), n);

            task_p1.fork();
            task_p2.fork();
            task_p3.fork();
            task_p4.fork();
            task_p5.fork();
            task_p6.fork();
            task_p7.fork();

            long[][] p1 = task_p1.join();
            long[][] p2 = task_p2.join();
            long[][] p3 = task_p3.join();
            long[][] p4 = task_p4.join();
            long[][] p5 = task_p5.join();
            long[][] p6 = task_p6.join();
            long[][] p7 = task_p7.join();

            long[][] c11 = summation(summation(p1, p4), subtraction(p7, p5));
            long[][] c12 = summation(p3, p5);
            long[][] c21 = summation(p2, p4);
            long[][] c22 = summation(subtraction(p1, p2), summation(p3, p6));

            return collectMatrix(c11, c12, c21, c22);
        }

    }

    //******************************************************************************************

    public static long[][] multiStrassenForkJoin(long[][] a, long[][] b) {

        int nn = getNewDimension(a, b);
        long[][] a_n = addition2SquareMatrix(a, nn);
        long[][] b_n = addition2SquareMatrix(b, nn);

        myRecursiveTask task = new myRecursiveTask(a_n, b_n, nn);
        ForkJoinPool pool = new ForkJoinPool();
        long[][] fastFJ = pool.invoke(task);

        return getSubmatrix(fastFJ, a.length, b[0].length);
    }

    //******************************************************************************************

    @Deprecated
    /**
     * Single-threaded matrix multiplication
     * algorithm by Strassen
     * */
    private static long[][] multiStrassen(long[][] a, long[][] b, int n) {
        if (n <= 128) {
            return multiplyTransposed(a, b);
        }

        n = n >> 1;
        ArrayList<Object> objects = new ArrayList<>();

        long[][] a11 = new long[n][n];
        long[][] a12 = new long[n][n];
        long[][] a21 = new long[n][n];
        long[][] a22 = new long[n][n];

        long[][] b11 = new long[n][n];
        long[][] b12 = new long[n][n];
        long[][] b21 = new long[n][n];
        long[][] b22 = new long[n][n];

        splitMatrix(a, a11, a12, a21, a22);
        splitMatrix(b, b11, b12, b21, b22);

        long[][] p1 = multiStrassen(summation(a11, a22), summation(b11, b22), n);
        long[][] p2 = multiStrassen(summation(a21, a22), b11, n);
        long[][] p3 = multiStrassen(a11, subtraction(b12, b22), n);
        long[][] p4 = multiStrassen(a22, subtraction(b21, b11), n);
        long[][] p5 = multiStrassen(summation(a11, a12), b22, n);
        long[][] p6 = multiStrassen(subtraction(a21, a11), summation(b11, b12), n);
        long[][] p7 = multiStrassen(subtraction(a12, a22), summation(b21, b22), n);

        long[][] c11 = summation(summation(p1, p4), subtraction(p7, p5));
        long[][] c12 = summation(p3, p5);
        long[][] c21 = summation(p2, p4);
        long[][] c22 = summation(subtraction(p1, p2), summation(p3, p6));

        return collectMatrix(c11, c12, c21, c22);
    }

    //******************************************************************************************

    private static int log2(int x) {
        int result = 1;
        while ((x >>= 1) != 0) {
            result++;
        }

        return result;
    }

    //******************************************************************************************

    private static int getNewDimension(long[][] a, long[][] b) {
        return 1 << log2(Collections.max(Arrays.asList(a.length, a[0].length, b[0].length)));
    }

    //******************************************************************************************

    static long[][] randomMatrix(int m, int n) {
        long[][] a = new long[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = new Random().nextInt(100);
            }
        }
        return a;
    }

    //******************************************************************************************

    static void printMatrix(long[][] a) {
        for (int i = 0; i < a[0].length; i++) {
            System.out.print("-------");
        }
        System.out.println();
        for (int[] anA : a) {
            System.out.print("|");
            for (int anAnA : anA) {
                System.out.printf("%4d |", anAnA);
            }

            System.out.println();
            for (int i = 0; i < a[0].length; i++) {
                System.out.print("-------");
            }
            System.out.println();
        }
    }

    //******************************************************************************************

    public static void test(int n, int m, int l) {

        long[][] a = randomMatrix(n, l);
        long[][] b = randomMatrix(l, m);
        long start, end;

        //****************************************
        //	TEST 1
        start = System.currentTimeMillis();
        long[][] matrixByStrassenFJ = multiStrassenForkJoin(a, b);
        end = System.currentTimeMillis();
        System.out.printf("Strassen Fork-Join Multiply [A:%dx%d; B:%dx%d]: \tElapsed: %dms\n", n, l, l, m, end - start);
        //****************************************

        //****************************************
        //	TEST 2
        start = System.currentTimeMillis();
        int nn = getNewDimension(a, b);

        long[][] a_n = addition2SquareMatrix(a, nn);
        long[][] b_n = addition2SquareMatrix(b, nn);

        long[][] temp = multiStrassen(a_n, b_n, nn);
        long[][] matrixByStrassen = getSubmatrix(temp, n, m);
        end = System.currentTimeMillis();
        System.out.printf("Strassen Multiply [A:%dx%d; B:%dx%d]: \tElapsed: %dms\n", n, l, l, m, end - start);
        //****************************************

        //****************************************
        //	TEST 3
        start = System.currentTimeMillis();
        long[][] matrixByUsual = multiply(a, b);
        end = System.currentTimeMillis();
        System.out.printf("Usual Multiply [A:%dx%d; B:%dx%d]: \tElapsed: %dms\n", n, l, l, m, end - start);
        //****************************************

        //****************************************
        //	TEST 4
        start = System.currentTimeMillis();
        long[][] matrixByUsualTransposed = multiplyTransposed(a, b);
        end = System.currentTimeMillis();
        System.out.printf("Usual Multiply Transposed [A:%dx%d; B:%dx%d]: \tElapsed: %dms\n", n, l, l, m, end - start);
        //****************************************

        System.out.println("Matrices are equal: " + Arrays.deepEquals(matrixByStrassenFJ, matrixByStrassen));
        System.out.println("Matrices are equal: " + Arrays.deepEquals(matrixByStrassenFJ, matrixByUsual));
        System.out.println("Matrices are equal: " + Arrays.deepEquals(matrixByStrassenFJ, matrixByUsualTransposed));

    }

    //******************************************************************************************

    private static class Multipliers {
        private final long[][] matrixA;
        private final long[][] matrixB;

        public Multipliers(long[][] a, long[][] b) {
            matrixA = a;
            matrixB = b;
        }

        public long[][] getMatrixB() {
            return matrixB;
        }

        public long[][] getMatrixA() {
            return matrixA;
        }
    }

    //******************************************************************************************

    private static Multipliers validation(String[] args) {
        int rowsA;
        int columnsA;
        int rowsB;
        int columnsB;

        if (args.length < 6) {
            throw new IllegalArgumentException("Too few parameters. Should be not less then 6.");
        }

    	/*
         * Note: method parseInt returns NumberFormatException if the argument String
    	 * does not contain a parsable int
    	 * */

        rowsA = Integer.parseInt(args[0]);
        columnsA = Integer.parseInt(args[1]);
        rowsB = Integer.parseInt(args[2]);
        columnsB = Integer.parseInt(args[3]);

        if (rowsA <= 0 || columnsA <= 0 || rowsB <= 0 || columnsB <= 0) {
            throw new IllegalArgumentException("Array dimension can't be negative or zero");
        }

        if (args.length - (rowsA * columnsA + rowsB * columnsB) != 4) {
            throw new IllegalArgumentException("Incorrect number of values to initialize two arrays.");
        }

        if (columnsA != rowsB) {
            throw new IllegalArgumentException("The number of columns of the matrix A is not equal to the number of rows of the matrix B.");
        }

        long[][] a = new long[rowsA][columnsA];
        long[][] b = new long[rowsB][columnsB];

        int k = 4;

        //***************************************

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                a[i][j] = Integer.parseInt(args[k++]);
            }
        }

        //***************************************

        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                b[i][j] = Integer.parseInt(args[k++]);
            }
        }

        //***************************************

        return new Multipliers(a, b);
    }

    //******************************************************************************************

    /*
        Матрицы подаются как аргументы программы в следующем формате
        N M X Y A_1_1 ... A_N_M B_1_1 ... B_X_Y

        где N и M - размерность первой матрицы A,
        A_1_1 ... A_N_M - элементы матрицы A,
        X и Y - размерность второй матрицы B,
        B_1_1 ... B_X_Y - элементы матрицы B.

        Например, для умножения единичной матрицы размером 2 на 2 на вектор (-1, -1)
        необходимо на вход приложению пожать следующие аргументы
        2 2 2 1 1 0 0 1 -1 -1
        В консоль должен распечататься вектор:
        -1
        -1
    */
    public static void main(String[] args) {
        Multipliers multipliers = validation(args);

        long[][] matrixByStrassenFJ = multiStrassenForkJoin(multipliers.getMatrixA(), multipliers.getMatrixB());
        long[][] matrixByUsual = multiply(multipliers.getMatrixA(), multipliers.getMatrixB());

        printMatrix(matrixByStrassenFJ);
        printMatrix(matrixByUsual);

        System.out.println(Arrays.deepEquals(matrixByStrassenFJ, matrixByUsual));
    }

}
