package primary;



/**
 * This class if for underline data structure for conway application.
 * It's is responsible for setting rules, data initiation, calculate cell live/die for every timestep.
 * @author Haijin He
 *
 */


public class ConwayData
{
  
  static final int N = 30;
  
  //bigger size to avoid edge check.
  boolean conway[][][] = new boolean[N + 2][N + 2][N + 2];
  {
    for (int i = 0; i <= N +1; i++)
      for (int j = 0; j <= N+1 ; j++)
        for (int k = 0; k <= N+1; k++)
          conway[i][j][k]=false;
  }
  boolean conwaynext[][][] = new boolean[N + 2][N + 2][N + 2];

  private int r1, r2, r3, r4;

  // r3 > r4 r1< r2
  /**
   * sets the rules for evolution.
   * @param r1  rule 1
   * @param r2  rule 2
   * @param r3  rule 3
   * @param r4  rule 4
   */
  public void setRule(int r1, int r2, int r3, int r4)
  {
    this.r1 = r1;
    this.r2 = r2;
    this.r3 = r3;
    this.r4 = r4;

  }
  
  /**
   * empty constructor. now it prints number of living cells for better tracking.
   */
  public ConwayData()
  {
    count(conway);
  }
/**
 * constructor. initilize data. cell is alive if randomly below a given percentage.
 * @param percent  
 */
  public ConwayData(double percent)
  {

    for (int i = 1; i <= N ; i++)
      for (int j = 1; j <= N ; j++)
        for (int k = 1; k <= N; k++)
        {
         // if (i == 0 || i == N + 1 || j == 0 || j == N + 1 || k == 0 || k == N + 1)
         //   conway[i][j][k] = false;
           if (Math.random() <= percent)
            conway[i][j][k] = true;
          else
            conway[i][j][k] = false;

        }
    count(conway);
    
    for (int i = 1; i <= N; i++)
      for (int j = 1; j <= N; j++)
        for (int k = 1; k <= N; k++)
        {
          conwaynext[i][j][k] = conway[i][j][k];
        }

  }


  /**
   * calculate next time step based on current data and rules.
   */
  public void nextTimeStep()
  {

    // make the Diff matrix to be 0;

    // copy the whole matrix to next matrix
    for (int i = 1; i <= N; i++)
      for (int j = 1; j <= N; j++)
        for (int k = 1; k <= N; k++)
        {
          conwaynext[i][j][k] = conway[i][j][k];
        }
   
    // change of next matrix based on old matrix
    for (int i = 1; i <= N; i++)
      for (int j = 1; j <= N; j++)
        for (int k = 1; k <= N; k++)
        {
          int livingNeighbor = 0;
          for (int l = -1; l <= 1; l++)
            for (int m = -1; m <= 1; m++)
              for (int n = -1; n <= 1; n++)
              {
                if (conway[i + l][j + m][k + n] == true)
                  livingNeighbor++;
              }
          if (conway[i][j][k] == true)
            livingNeighbor--;
         
         
          // cell will reborn. r1<r2. only in r1-r2 range will cell reborn.
          if (conway[i][j][k] == false && livingNeighbor <= r2 && livingNeighbor >= r1)
          {
            conwaynext[i][j][k] = true;
        //    System.out.println(i +" "+ j+ " "+ k + " "+ livingNeighbor);
          }
          
          // cell will die r3 >r4. bigger than bigger, small than smaller.
          if (conway[i][j][k] == true && (livingNeighbor >= r3 || livingNeighbor <= r4))
          {
            conwaynext[i][j][k] = false;
          }
          
        }
    count(conway);
    // swap reference
    boolean[][][] p = conway;
    conway = conwaynext;
    conwaynext = p;

    count(conway);
 
  }
 /**
  * counts the number of living cells in the given matrix.
  * @param matrix
  */
  public void count(boolean[][][] matrix)
  {
    int count = 0;
    for (int i = 1; i <= N; i++)
      for (int j = 1; j <= N; j++)
        for (int k = 1; k <= N; k++)
        {
          if (matrix[i][j][k] == true)
            count++;
        }
    System.out.println("current number of live cells " + count);

  }

  public static void main(String[] args)
  {
    // TODO Auto-generated method stub
    ConwayData con = new ConwayData();
    con.setRule(0, 28, 28 , -1);
   // System.out.println(con.conway[10][10][10]);
    for (int i = 0; i <1; i++)
    {
      
     // System.out.println(con.conway[17][17][3]);
      con.nextTimeStep();
    }

  }

}
