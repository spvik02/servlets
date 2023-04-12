# servlets

Конфигураци томкат. Для подключения использовала плагин SmartTomcat
![image](https://user-images.githubusercontent.com/111181469/231342749-eeb9ec7a-8549-4a3f-ae29-f18d6537839a.png)

В файле application.yml следует указать url (например, jdbc:postgresql://localhost:5432/store), user, password. 
Так же следует указать true/false для полей database.create и database.insert. 
Если create: true, то при запуске приложения будут созданы таблицы. Если insert:true, то при запуске приложения таблицы будут заполнены данными
Если create/insert не заданы, то значение по умолчанию - false.

Поиск по айди: метод get, вместо 1 может быть указан другой id
- http://localhost:8080/api/discountCards/1
- http://localhost:8080/api/products/1
- http://localhost:8080/api/stocks/1
- http://localhost:8080/api/receipts/1

Вывод всех данных: метод get
- http://localhost:8080/api/discountCards
- http://localhost:8080/api/products
- http://localhost:8080/api/stocks
- http://localhost:8080/api/receipts

Для добавления: метод post и в теле указываем тело. Данные следует указывать внутри узла "data":
- http://localhost:8080/api/products
  - {
    "data":{ 
        "id": 3, 
        "name": "apples", 
        "price": "3",
        "isAtDiscount": true 
    }
}
- с остальными url как у get методов, только уже с телом

Для обновления метод put и в теле указываем тело. Данные следует указывавать внутри узла "data"

Для удаления метод delete, url как у поиск по айди.

Для получения pdf api/receipts/pdf/ и далее набор параметров productId-qty разделенных &. Если была предоставлена скидочкая карта, то в конце следует добавить &Card-cardId
- http://localhost:8080/api/receipts/pdf/1-2&2-5&3-5&4-2&5-5&6-2&Card-1
![image](https://user-images.githubusercontent.com/111181469/231346043-272f7e75-cce5-459b-9582-17b34e493664.png)
- http://localhost:8080/api/receipts/pdf/1-2&2-5&3-5&19-2&5-5&6-2&Card-1 ← не существует товара с id 19 (4 пара)
![image](https://user-images.githubusercontent.com/111181469/231346511-eb0da113-a315-4701-913b-edb28c05c210.png)


Пагинация. Если параметры не будут переданы будут использованы дефолтные значения page=1 pagesize=20
- http://localhost:8080/api/products?page=2&pagesize=3
