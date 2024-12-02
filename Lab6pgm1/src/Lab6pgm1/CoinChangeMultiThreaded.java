package Lab6pgm1;
import java.util.Scanner;
import java.util.concurrent.*;

public class CoinChangeMultiThreaded {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Scanner scanner = new Scanner(System.in);

        // Input values from the user
        System.out.print("Enter the number of coins (N): ");
        int N = scanner.nextInt();

        System.out.print("Enter the target sum: ");
        int targetSum = scanner.nextInt();

        System.out.println("Enter the coin denominations: ");
        int[] coins = new int[N];
        for (int i = 0; i < N; i++) {
            coins[i] = scanner.nextInt();
        }

        scanner.close();

        // Call the multithreaded method to compute the number of ways
        int result = countWaysMultithreaded(coins, targetSum);
        System.out.println("Number of ways to make the sum: " + result);
    }

    public static int countWaysMultithreaded(int[] coins, int targetSum) throws InterruptedException, ExecutionException {
        // Create a global DP array to store the results
        int[] dp = new int[targetSum + 1];
        dp[0] = 1; // Base case: There's 1 way to make sum 0 (using no coins)

        // Number of threads to use
        int numThreads = Math.min(coins.length, Runtime.getRuntime().availableProcessors());
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Submit tasks to process coins in parallel
        for (int coin : coins) {
            executor.submit(() -> {
                // Update the dp array for this coin
                for (int j = coin; j <= targetSum; j++) {
                    synchronized (dp) {
                        dp[j] += dp[j - coin];
                    }
                }
            });
        }

        // Shutdown the executor
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // The result is stored in dp[targetSum]
        return dp[targetSum];
    }
}
