package edu.citu.procrammers.eva.models.strategy.hashtable;

public class MadStrategy implements CompressionStrategy {
    private int a, b, p, N;

   public MadStrategy(int a, int b, int p, int N) {
       this.a = a;
       this.b = b;
       this.p = p;
       this.N = N;
   }

   @Override
    public int compress(int key) {
       return (a * key + b) % p % N;
   }
}
