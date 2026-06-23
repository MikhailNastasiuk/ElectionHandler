# ElectionHandler Application

## Created for test purposes

## Task description:
### (in polish)
Opis zadania:
Przygotuj prostą aplikację w Spring Boot, która umożliwia rejestrację głosów w wielu wyborach.
Aplikacja powinna spełniać następujące wymagania:

Zarządzanie wyborcami

1. Dodawanie nowych wyborców
2. Blokowanie/odblokowywanie wyborców

Zarządzanie wyborami

1. Każde wybory to osobna instancja (np. „Wybory na Wójta w 2025”)
2. Możliwość definiowania opcji wyboru (np. kandydatów)

Głosowanie

1. Wyborca może oddać głos w danej instancji wyborów tylko raz
2. Każda instancja wyborów ma własne opcje głosowania

API REST

1. Aplikacja powinna wystawiać odpowiednie endpointy REST
2. Dane zapisywane w bazie danych (dowolnej relacyjnej, np. PostgreSQL)

Dodatkowe wskazówki:

1. Zastosuj dobre praktyki i wzorce projektowe (np. warstwowa architektura, DTO, walidacje).
2. Kod powinien być czytelny i dobrze zorganizowany.
3. Nie oczekujemy pełnej implementacji zabezpieczeń czy frontendu – skup się na logice i strukturze aplikacji.