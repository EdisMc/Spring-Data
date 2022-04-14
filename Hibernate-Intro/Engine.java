import entities.Employee;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

public class Engine implements Runnable {

    private final EntityManager entityManager;
    private final BufferedReader bufferedReader;

    public Engine(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        System.out.println("Select exercise number:");

        try {
            int exNumber = Integer.parseInt(bufferedReader.readLine());
            switch (exNumber) {
                case 2:
                    exTwo();
                case 3:
                    exThree();
                case 4:
                    exFour();
                case 5:
                    exFive();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }

    }

    private void exTwo() {
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("UPDATE Town t " +
                "SET t.name = upper(t.name) " +
                "WHERE length(t.name) <= 5 ");

        System.out.println(query.executeUpdate());

        entityManager.getTransaction().commit();
    }

    private void exThree() throws IOException {
        System.out.println("Enter employee full name:");
        String[] fullName = bufferedReader.readLine().split("\\s+");
        String firstName = fullName[0];
        String lastName = fullName[1];

        Long singleResult = entityManager.createQuery("SELECT count(e) FROM Employee e " +
                "WHERE e.firstName = :f_name AND e.lastName = :l_name", Long.class)
                .setParameter("f_name", firstName)
                .setParameter("l_name", lastName)
                .getSingleResult();

        System.out.println(singleResult == 0
            ? "No" : "Yes");
    }

    private void exFour() {
        entityManager.createQuery("SELECT e FROM Employee e " +
                "WHERE e.salary > :min_salary", Employee.class)
                .setParameter("min_salary", BigDecimal.valueOf(50000L))
                .getResultStream()
                .map(Employee::getFirstName)
                .forEach(System.out::println);
    }

    private void exFive() {
        entityManager.createQuery("SELECT e FROM Employee e " +
                        "WHERE e.department.name = :d_name " +
                        "ORDER BY e.salary, e.id", Employee.class)
                .setParameter("d_name", "Research and Development")
                .getResultStream()
                .forEach(employee -> {
                    System.out.printf("%s %s from %s - $%.2f%n",
                            employee.getFirstName(),
                            employee.getLastName(),
                            employee.getDepartment().getName(),
                            employee.getSalary());
                });
    }

}
