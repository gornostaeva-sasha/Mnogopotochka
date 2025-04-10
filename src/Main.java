
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.LockSupport; 

public class Main {

    public static void main(String[] args) {
        
        Map<Integer, Runnable> tasks = new HashMap<>();
        tasks.put(1, Main::task1);
        tasks.put(2, Main::task2);
        tasks.put(3, Main::task3);
        tasks.put(4, Main::task4);
        tasks.put(5, Main::task5);
        tasks.put(6, Main::task6);
        tasks.put(7, Main::task7);
        tasks.put(8, Main::task8);
        tasks.put(9, Main::task9);
        tasks.put(10, Main::task10);
        tasks.put(11, Main::task11);
        tasks.put(12, Main::task12);


        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Выберите задачу (1-12) или 0 для выхода:");
            Integer choice = null;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Неверный ввод. Пожалуйста, введите число.");
                continue;
            }

            if (choice == 0) {
                return;
            } else if (tasks.containsKey(choice)) {
                tasks.get(choice).run();
            } else {
                System.out.println("Неверный выбор, попробуйте снова.");
            }
        }
    }

    // Задача 1: Синхронизация счётчика
    public static void task1() {
        withCounter(5, 1000);
    }

    private static void withCounter(int threads, int iterations) {
        AtomicInteger counter = new AtomicInteger(0);
        ReentrantLock lock = new ReentrantLock();

        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    lock.lock();
                    try {
                        counter.incrementAndGet();
                    } finally {
                        lock.unlock();
                    }
                }
            });
            threadList.add(thread);
            thread.start();
        }

        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Итоговое значение счётчика: " + counter.get());
    }

    // Задача 2: Потокобезопасный список
    public static void task2() {
        withThreadSafeList(10, 100);
    }

    private static void withThreadSafeList(int threads, int itemsPerThread) {
        List<Integer> list = new CopyOnWriteArrayList<>();

        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < itemsPerThread; j++) {
                    list.add(j + 1);
                }
            });
            threadList.add(thread);
            thread.start();
        }

        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Размер списка: " + list.size());
    }

    // Задача 3: Пул потоков
    public static void task3() {
        withThreadPool(20, 4);
    }

    private static void withThreadPool(int tasks, int poolSize) {
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        for (int i = 1; i <= tasks; i++) {
            final int taskNumber = i; 
            executor.submit(() -> {
                System.out.println("Задача " + taskNumber + " выполняется в потоке " + Thread.currentThread().getName());
            });
        }

        executor.shutdown(); 
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Задача 4: Симуляция работы банка
    public static void task4() {
        withBankSimulation(3, 10);
    }

    private static void withBankSimulation(int accountsCount, int transactions) {
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < accountsCount; i++) {
            accounts.add(new Account(1000 * (i + 1)));
        }

        ExecutorService executor = Executors.newFixedThreadPool(4);
        Random random = new Random();
        for (int i = 0; i < transactions; i++) {
            executor.submit(() -> {
                Account from = accounts.get(random.nextInt(accountsCount));
                Account to = accounts.get(random.nextInt(accountsCount));
                if (from != to) {
                    from.transfer(to, 100);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < accounts.size(); i++) {
            System.out.println("Аккаунт " + i + ": " + accounts.get(i).getBalance());
        }
    }

    static class Account {
        private int balance;
        private final ReentrantLock lock = new ReentrantLock();

        public Account(int balance) {
            this.balance = balance;
        }

        public int getBalance() {
            return balance;
        }

        public void transfer(Account target, int amount) {
            lock.lock();
            try {
                if (balance >= amount) {
                    balance -= amount;
                    target.balance += amount;
                }
            } finally {
                lock.unlock();
            }
        }
    }

    // Задача 5: CyclicBarrier
    public static void task5() {
        withBarrier(5);
    }

    private static void withBarrier(int parties) {
        CyclicBarrier barrier = new CyclicBarrier(parties, () -> {
            System.out.println("Все потоки достигли барьера");
        });

        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < parties; i++) {
            final int threadNumber = i;
            Thread thread = new Thread(() -> {
                System.out.println("Поток " + threadNumber + " выполняет задачу");
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println("Поток " + threadNumber + " продолжает выполнение");
            });
            threadList.add(thread);
            thread.start();
        }

        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Задача 6: Semaphore
    public static void task6() {
        withSemaphore(5, 2);
    }

    private static void withSemaphore(int threads, int permits) {
        Semaphore semaphore = new Semaphore(permits);

        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            final int threadNumber = i;
            Thread thread = new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println("Поток " + threadNumber + " получил доступ к ресурсу");
                    Thread.sleep(1000);
                    System.out.println("Поток " + threadNumber + " освобождает ресурс");
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threadList.add(thread);
            thread.start();
        }

        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Задача 7: Callable и Future
    public static void task7() {
        withCallable(10, 10);
    }

    private static void withCallable(int tasks, int poolSize) {
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        List<Future<Integer>> futures = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < tasks; i++) {
            Callable<Integer> task = () -> {
                int randomNumber = random.nextInt(100);
                System.out.println("Callable задача выполняется в потоке " + Thread.currentThread().getName() + ", случайное число: " + randomNumber);
                return randomNumber;
            };
            futures.add(executor.submit(task));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int sum = 0;
        for (Future<Integer> future : futures) {
            try {
                sum += future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Сумма результатов Callable задач: " + sum);
    }

    // Задача 8: CountDownLatch
    public static void task8() {
        withCountDownLatch(5);
    }

    private static void withCountDownLatch(int threads) {
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            final int threadNumber = i;
            new Thread(() -> {
                System.out.println("Поток " + threadNumber + " начал выполнение");
                try {
                    Thread.sleep((long) (Math.random() * 2000));  
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Поток " + threadNumber + " завершил выполнение");
                latch.countDown();
            }).start();
        }

        try {
            latch.await(); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Все потоки завершили выполнение. CountDownLatch достиг нуля.");
    }

    // Задача 9: Exchanger (Простая версия с двумя потоками)
    public static void task9() {
        withExchanger();
    }

    private static void withExchanger() {
        Exchanger<String> exchanger = new Exchanger<>();

        new Thread(() -> {
            String message = "Сообщение от потока 1";
            try {
                System.out.println("Поток 1 отправляет: " + message);
                message = exchanger.exchange(message); 
                System.out.println("Поток 1 получил: " + message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            String message = "Сообщение от потока 2";
            try {
                System.out.println("Поток 2 отправляет: " + message);
                message = exchanger.exchange(message); 
                System.out.println("Поток 2 получил: " + message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Задача 10: LockSupport (Демонстрация простого парковки и разблокировки)
    public static void task10() {
        withLockSupport();
    }

    private static void withLockSupport() {
        Thread thread = new Thread(() -> {
            System.out.println("Поток начал выполнение и будет заблокирован");
            LockSupport.park(); 
            System.out.println("Поток разблокирован и продолжает выполнение");
        });

        thread.start();

        try {
            Thread.sleep(2000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Разблокируем поток");
        LockSupport.unpark(thread); 
    }


    // Задача 11: ForkJoinPool (Простая задача суммирования массива)
    public static void task11() {
        withForkJoinPool();
    }

    private static void withForkJoinPool() {
        int[] array = new int[1000];
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(10); 
        }

        ForkJoinPool pool = new ForkJoinPool();
        SumArray task = new SumArray(array, 0, array.length);
        long sum = pool.invoke(task); // Запускаем задачу
        System.out.println("Сумма элементов массива: " + sum);
    }


    // Задача 12: CompletableFuture (Простая цепочка асинхронных операций)
    public static void task12() {
        withCompletableFuture();
    }

    private static void withCompletableFuture() {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("Первая операция в потоке: " + Thread.currentThread().getName());
            return "Привет";
        }).thenApply(s -> {
            System.out.println("Вторая операция в потоке: " + Thread.currentThread().getName());
            return s + ", мир!";
        }).thenAccept(s -> {
            System.out.println("Третья операция в потоке: " + Thread.currentThread().getName());
            System.out.println("Результат: " + s);
        });

        try {
            future.get(); 
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}

// Вспомогательный класс для ForkJoinPool (Задача 11)
class SumArray extends RecursiveTask<Long> {
    private final int[] array;
    private final int start;
    private final int end;
    private static final int THRESHOLD = 100;

    public SumArray(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) {
           
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        } else {
            
            int middle = (start + end) / 2;
            SumArray left = new SumArray(array, start, middle);
            SumArray right = new SumArray(array, middle, end);

            left.fork(); 
            long rightSum = right.compute(); 
            long leftSum = left.join(); 
            return leftSum + rightSum;
        }
    }
}
