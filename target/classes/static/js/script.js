// GET запрос для приветственного сообщения
fetch('/api/prices') // Отправляет GET запрос на /api/prices для получения приветственного сообщения.
    .then(response => response.text()) // Обрабатывает ответ и преобразует его в текст.
    .then(data => {
        document.getElementById('welcomeMessage').innerText = data; // Вставляем текст в элемент с id 'welcomeMessage'
    })
    .catch(error => console.error('Error:', error)); // Ловим ошибки, если они есть

// Обработчик отправки формы для загрузки файла
document.getElementById('uploadForm').addEventListener('submit', function(event) { // Назначает обработчик события отправки формы
    event.preventDefault(); // Предотвращаем стандартное поведение отправки формы

    var formData = new FormData(); // Создаем объект FormData для передачи данных формы
    formData.append('file', document.getElementById('fileInput').files[0]); // Добавляет выбранный файл в объект FormData.

    fetch('/api/prices/upload', { // Отправляет POST запрос на /api/prices/upload для загрузки файла.
        method: 'POST', // Отправляем POST запрос
        body: formData // Передаем объект FormData в теле запроса
    })
        .then(response => response.text()) // Обрабатывает текстовый ответ от сервера после загрузки файла.
        .then(data => {
            var uploadMessage = document.getElementById('uploadMessage');
            if (data.includes('Failed to upload and process file.')) {
                uploadMessage.style.color = 'red'; // Устанавливаем красный цвет текста для ошибки
            } else {
                uploadMessage.style.color = 'green'; // Устанавливаем зелёный цвет текста для успешного ответа
            }
            uploadMessage.innerText = data; // Вставляем текст ответа в элемент с id 'uploadMessage'
            fetchResults(); // Обновляем результаты после загрузки файла
        })
        .catch(error => {
            var uploadMessage = document.getElementById('uploadMessage');
            uploadMessage.style.color = 'red'; // Устанавливаем красный цвет текста для ошибки
            uploadMessage.innerText = 'Failed to upload and process file.'; // Выводим сообщение об ошибке загрузки
            console.error('Error:', error); // Логируем ошибку в консоль
        });
});

function fetchResults() {
    // GET запрос для средней цены
    fetch('/api/prices/calculateAveragePrice')
        .then(response => response.json())
        .then(data => {
            document.getElementById('averagePrice').innerText = `Средняя цена: ${data}`;
        })
        .catch(error => {
            document.getElementById('averagePrice').innerText = 'Средняя цена: Загрузите статистику для вывода результата';
            console.error('Error:', error);
        });

    // GET запрос для линейного предсказания
    fetch('/api/prices/predict/linear')
        .then(response => response.json())
        .then(data => {
            document.getElementById('linearPrediction').innerText = `Линейное предсказание следующей цены: ${data}`;
        })
        .catch(error => {
            document.getElementById('linearPrediction').innerText = 'Линейное предсказание следующей цены: Загрузите статистику для вывода результата';
            console.error('Error:', error);
        });
}

// Обработчики событий для изменения периода SMA и EMA
document.getElementById('smaPeriod').addEventListener('input', function() {
    const smaPeriod = this.value;
    if (smaPeriod) {
        fetch(`/api/prices/predict/sma?period=${smaPeriod}`)
            .then(response => response.json())
            .then(data => {
                document.getElementById('smaPrediction').innerText = `Предсказание следующей цены с помощью SMA: ${data}`;
            })
            .catch(error => {
                document.getElementById('smaPrediction').innerText = 'Предсказание следующей цены с помощью SMA: Ошибка при загрузке данных';
                console.error('Error:', error);
            });
    } else {
        document.getElementById('smaPrediction').innerText = 'Предсказание следующей цены с помощью SMA: Введите период';
    }
});

document.getElementById('emaPeriod').addEventListener('input', function() {
    const emaPeriod = this.value;
    if (emaPeriod) {
        fetch(`/api/prices/predict/ema?period=${emaPeriod}`)
            .then(response => response.json())
            .then(data => {
                document.getElementById('emaPrediction').innerText = `Предсказание следующей цены с помощью EMA: ${data}`;
            })
            .catch(error => {
                document.getElementById('emaPrediction').innerText = 'Предсказание следующей цены с помощью EMA: Ошибка при загрузке данных';
                console.error('Error:', error);
            });
    } else {
        document.getElementById('emaPrediction').innerText = 'Предсказание следующей цены с помощью EMA: Введите период';
    }
});

function downloadFile(type) {
    const anomalyCoefficient = document.getElementById('anomalyCoefficient').value;
    const url = `/api/prices/download/${type}?anomalyCoefficient=${anomalyCoefficient}`;
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.blob();
        })
        .then(blob => {
            const url = window.URL.createObjectURL(new Blob([blob]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `${type}.xlsx`);
            document.body.appendChild(link);
            link.click();
            link.parentNode.removeChild(link);
        })
        .catch(error => console.error('Error:', error));
}

function openInNewPage(type) {
    const anomalyCoefficient = document.getElementById('anomalyCoefficient').value;
    const url = `/api/prices/view/${type}?anomalyCoefficient=${anomalyCoefficient}`;
    fetch(url)
        .then(response => response.json())
        .then(data => {
            localStorage.setItem('anomaliesData', JSON.stringify(data));
            window.open(`${type}.html`, '_blank');
        })
        .catch(error => console.error('Error:', error));
}

// Устанавливаем начальное сообщение
document.getElementById('averagePrice').innerText = 'Средняя цена: Загрузите статистику для вывода результата';
document.getElementById('linearPrediction').innerText = 'Линейное предсказание следующей цены: Загрузите статистику для вывода результата';
document.getElementById('arimaPrediction').innerText = 'Предсказание следующей цены с помощью ARIMA: Загрузите статистику для вывода результата';
document.getElementById('smaPrediction').innerText = 'Предсказание следующей цены с помощью SMA: Загрузите статистику для вывода результата';
document.getElementById('emaPrediction').innerText = 'Предсказание следующей цены с помощью EMA: Загрузите статистику для вывода результата';
fetchResults(); // Начальная загрузка результатов