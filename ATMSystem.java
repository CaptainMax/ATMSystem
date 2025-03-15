import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ATMSystem {
    public static void main(String[] args) {
        Account account = new Account("12345678", "1234", 1000.0);

       // account.deposit(500);
        // account.withdraw(500);

        ATM atm = new ATM(account);
        // atm.start();
        atm.showMenu();

        // Runnable user1 = () -> {
        //     account.withdraw(500);
        //     account.deposit(200);
        // };

        // Runnable user2 = () -> {
        //     account.withdraw(700);
        //     account.deposit(300);
        // };

        // // 线程池，模拟多个用户同时访问
        // ExecutorService executor = Executors.newFixedThreadPool(2);
        // executor.execute(user1);
        // executor.execute(user2);

        // // 关闭线程池
        // executor.shutdown();
    }
}

class Account{
    private String accountNumber;
    private String pin;
    private double balance;

    public Account(String accountNumber, String pin, double balance){
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
    }

    public String getAccountNumber(){
        return accountNumber;
    }
    public boolean validPin(String enteredPin){
        return this.pin.equals(enteredPin);
    }
    public synchronized double getBalance(){
        return balance;
    }

    public synchronized void withdraw(double amount){
        if(amount <= balance){
            System.out.println(Thread.currentThread().getName() + " withdraw:$ " + amount);
            balance -= amount;
            System.out.println(Thread.currentThread().getName() + " current balance:$ " + balance);
        } else {
            System.out.println("Insufficient funds");
        }
    }

    public void deposit(double amount){
        System.out.println(Thread.currentThread().getName() + " deposit:$ " + amount);
        balance += amount;
        System.out.println(Thread.currentThread().getName() + " current balance:$ " + balance);
    }
}

class Authenticator{
    private Account account;
    public Authenticator(Account account){
        this.account = account;
    }

    public boolean authenticate(String enteredPin){
        return account.validPin(enteredPin);
    }
}

class ATM {
    private Account account;
    private Authenticator authenticator;
    private Scanner scanner;

    public ATM(Account account){
        this.account = account;
        this.authenticator = new Authenticator(account);
        this.scanner = new Scanner(System.in);
    }

    public void start(){
        System.out.println("Welcome to the ATM");
        System.out.println("Please enter account number");
        String accountNumber = scanner.nextLine();
        System.out.println("Enter PIN: ");
        String enteredPin = scanner.nextLine();

        if(authenticator.authenticate(enteredPin)){
            System.out.println("Authentication successful!");
        } else {
            System.out.println("Invalid PIN. Please try again");
        }
    }

    public void showMenu(){
        while(true){
            System.out.println("\nATM Menu:");
            System.out.println("1. Check balance");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Exit");
            System.out.println("Choose an option: ");

            int option = scanner.nextInt();
            switch (option) {
                case 1:
                    checkBalance();
                    break;
                case 2:
                    withdraw();
                    break;
                case 3:
                    deposit();
                    break;
                case 4:
                    System.out.println("Thank you for using the ATM");
                    return;
                default:
                    System.out.println("Invalid option. Please try again");
                    break;
            }
        }
    }
    private void checkBalance(){
        System.out.println("Your balance is: " + account.getBalance());
    }
    private void withdraw(){
        System.out.println("Enter withdrawal amount");
        if (scanner.hasNextDouble()) {  // 确保用户输入的是数字
            double amount = scanner.nextDouble();
            account.withdraw(amount);
            System.out.println("Withdrawal successful. New Balance: " + account.getBalance());
        } else {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();  // 清除错误输入
        }
    }
    private void deposit(){
        System.out.println("Enter deposit amount: ");
        if (scanner.hasNextDouble()) {  // 确保用户输入的是数字
            double amount = scanner.nextDouble();
            account.deposit(amount);
            System.out.println("Deposit successful. New Balance: " + account.getBalance());
        } else {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();  // 清除错误输入
        }
    }
}
