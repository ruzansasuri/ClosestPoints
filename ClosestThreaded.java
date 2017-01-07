/**
 * Created by Ruzan on 10/23/2016.
 */
public class ClosestThreaded extends Thread
{
    int size;
    double minDis;
    double points[][];
    double closePoints[][];
    long milliSeconds;
    long timeTaken;
    public ClosestThreaded(int n,double arr[][])
    {
        size = n;
        points = arr;
        minDis = Double.MAX_VALUE;
        closePoints = new double[2][arr[0].length];
    }
    public void init()
    {
        milliSeconds = System.currentTimeMillis();
    }
    public void end()
    {
        timeTaken = System.currentTimeMillis() - milliSeconds;
        for(int i = 0; i < closePoints.length; i++)
        {
            System.out.print("( ");
            for (int j = 0; j < closePoints[i].length; j++)
            {
                System.out.print(closePoints[i][j] + " ");
            }
            System.out.print(") ");
        }
        System.out.println(timeTaken + "ms");
    }
    public void run()
    {
        brute();
    }
    public double euclidian(double point1[],double point2[])
    {
        double distance = 0;
        for(int i = 0; i < point1.length; i++)
        {
            distance += Math.pow(point1[i]-point2[i],2);
        }
        distance = Math.sqrt(distance);
        if(distance == 0)
        {
            distance = Double.MAX_VALUE;
        }
        return distance;
    }
    public void crossing(double arr[][][])
    {
        double min = Double.MAX_VALUE;
        double storing[][] = new double[2][arr[0][0].length];
        for(int i = 0; i < arr.length - 1; i++)
        {
            for(int j = 0; j < arr[0].length; j++)
            {
                for(int k = i + 1; k < arr.length; k++)
                {
                    for(int l = 0; l < arr[k].length; l++)
                    {
                        double distance = euclidian(arr[i][j],arr[k][l]);
                        if(distance < min)
                        {
                            min = distance;
                            storing[0] = arr[i][j];
                            storing[1] = arr[k][l];
                        }
                    }
                }
            }
        }
        minDis = min;
        closePoints = storing;
    }
    public void brute()
    {
        double min = Double.MAX_VALUE;
        double[][] storing = new double[2][points[0].length];
        for(int i = 0; i < points.length - 1; i++)
        {
            for(int j = i; j < points.length; j++)
            {
                double distance = euclidian(points[i],points[j]);
                if(distance<min)
                {
                    min = distance;
                    storing[0] = points[i];
                    storing[1] = points[j];
                }
            }
        }
        minDis = min;
        closePoints = storing;
    }
    public void createThreads(int cores)
    {
        ClosestThreaded ts[] = new ClosestThreaded[cores];
        int n = (points.length + 1) / cores;
        int points_left = points.length;
        if(n < 2)
        {
            n = 2;
        }
        double setofarr[][][] = new double[cores][n][points[0].length];
        for(int i = 0; i < cores; i++)
        {
            double arr[][] = new double[n][points[0].length];
            int x = 0;
            for (int j = 0; j < n; j++)
            {
                if((i *n + j) >= points.length)
                {
                    arr[j] = points[i*n+j-1];
                }
                else
                {
                    arr[j] = points[i * n + j];
                    x++;
                }
            }
            points_left -= x;
            ts[i] = new ClosestThreaded(x, arr);
            ts[i].start();
            setofarr[i] = arr;
            if(points_left == 0)
            {
                break;
            }
        }
        crossing(setofarr);
        for(int i = 0; i < cores; i++)
        {
            if (ts[i] == null)
            {
                break;
            }
            try
            {
                ts[i].join();
                if(minDis > ts[i].minDis)
                {
                    minDis = ts[i].minDis;
                    closePoints = ts[i].closePoints;
                }
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    public static void main(String ag[])
    {
        int s = 0;
        if(ag.length == 0)
        {
            s = 4;
        }
        else
        {
            try
            {
                s = Integer.parseInt(ag[0]);
            }
            catch(NumberFormatException e)
            {
                System.err.print("Please Enter a NUMBER that fits within an integer variable.");
                e.printStackTrace();
                System.exit(1);
            }
        }
        if(s <= 1)
        {
            System.err.println("The number of points should be greater than 1.");
            System.exit(1);
        }
        int cores = Runtime.getRuntime().availableProcessors();
        double arr[][] = new double[s][3];
        for(int i = 0; i < arr.length; i++)
        {
            for(int j = 0; j < arr[0].length;j++)
            {
                arr[i][j] = Math.random()*s;//i-arr.length/2;
            }
        }
        System.out.println("Single Threads: ");
        ClosestThreaded csingle = new ClosestThreaded(s, arr);
        csingle.init();
        csingle.brute();
        csingle.end();
        System.out.println(cores + " Threads: ");
        ClosestThreaded c = new ClosestThreaded(s, arr);
        c.init();
        c.createThreads(cores - 1);
        c.end();
    }
}
