// package id.cs.ui.advprog.inthecost.model;

// import id.cs.ui.advprog.inthecost.observer.Observer;
// import id.cs.ui.advprog.inthecost.observer.Subject;
// import lombok.Getter;

// import java.util.HashSet;
// import java.util.Set;

// @Getter
// public class Kos implements Subject {
//     private String kosName;
//     private int availableRooms;

//     private final Set<Observer> observers = new HashSet<>();
//     private int previousAvailableRooms = -1;

//     public Kos(String kosName, int availableRooms) {
//         this.kosName = kosName;
//         this.availableRooms = availableRooms;
//         this.previousAvailableRooms = availableRooms;
//     }

//     public void setAvailableRooms(int rooms) {
//         if (rooms != this.availableRooms) {
//             this.previousAvailableRooms = this.availableRooms;
//             this.availableRooms = rooms;

//             if (this.previousAvailableRooms == 0 && rooms > 0) {
//                 notifyObservers();
//             }
//         }
//     }

//     @Override
//     public void addObserver(Observer observer) {
//         observers.add(observer);
//     }

//     @Override
//     public void removeObserver(Observer observer) {
//         observers.remove(observer);
//     }

//     @Override
//     public void notifyObservers() {
//         for (Observer observer : observers) {
//             observer.update(this);
//         }
//     }

//     public Set<String> getWishlistedBy() {
//         return Set.of(); // Dummy dulu, atau sesuaikan nanti
//     }
// }