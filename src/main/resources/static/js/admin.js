// Ждём полной загрузки DOM перед запуском
document.addEventListener("DOMContentLoaded", loadUsers);

// Функция загружает список пользователей с бэка и отображает их в таблице
function loadUsers() {
    fetch("/api/users") // GET-запрос к REST API
        .then(response => response.json()) // Парсим JSON
        .then(users => {
            const tableBody = document.querySelector("#userTableBody");
            tableBody.innerHTML = ""; // Очищаем тело таблицы перед добавлением новых строк

            users.forEach(user => {
                const roles = user.roles.map(r => r.name.replace("ROLE_", "")).join(", ");
                const row = `
    <tr>
        <td>${user.id}</td>
        <td>${user.firstName}</td>
        <td>${user.lastName}</td>
        <td>${user.age}</td>
        <td>${user.email}</td>
        <td>${roles}</td>
        <td>
            <button class="btn btn-info btn-sm edit-btn" data-user-id="${user.id}">Редактировать</button>
        </td>
        <td>
            <button class="btn btn-danger btn-sm delete-btn" data-user-id="${user.id}">Удалить</button>
        </td>
    </tr>
`;
                tableBody.insertAdjacentHTML("beforeend", row); // Добавляем строку в таблицу
            });
        });
}

// Обработчик всех кликов по документу
document.addEventListener("click", function (e) {
    // === УДАЛЕНИЕ ===
    if (e.target.classList.contains("delete-btn")) {
        const userId = e.target.getAttribute("data-user-id");

        fetch(`/api/users/${userId}`, {
            method: "DELETE" // DELETE-запрос к API
        })
            .then(response => {
                if (response.ok) {
                    loadUsers(); // Перезагрузка списка пользователей
                }
            })
            .catch(error => {
                console.error("Ошибка удаления:", error); // В случае ошибки — выводим в консоль
            });
    }

    // === РЕДАКТИРОВАНИЕ ===
    if (e.target.classList.contains("edit-btn")) {
        const userId = e.target.getAttribute("data-user-id");

        fetch(`/api/users/${userId}`) // Получаем данные одного пользователя
            .then(res => res.json())
            .then(user => {
                // Заполняем форму редактирования
                document.getElementById("editUserId").value = user.id;
                document.getElementById("editFirstName").value = user.firstName;
                document.getElementById("editLastName").value = user.lastName;
                document.getElementById("editAge").value = user.age;
                document.getElementById("editEmail").value = user.email;

                // Получаем имена ролей пользователя
                const userRoleNames = user.roles.map(r => r.name);
                loadAllRoles(userRoleNames); // Загружаем роли и отмечаем нужные

                // Показываем модальное окно Bootstrap
                const modal = new bootstrap.Modal(document.getElementById("editUserModal"));
                modal.show();
            });
    }
});

// === ЗАГРУЗКА ВСЕХ ДОСТУПНЫХ РОЛЕЙ ===
function loadAllRoles(selectedRoles = []) {
    fetch("/api/roles") // Получаем все роли с сервера
        .then(response => response.json())
        .then(roles => {
            const select = document.getElementById("editRoles");
            select.innerHTML = ""; // Очищаем select перед заполнением

            roles.forEach(role => {
                const option = document.createElement("option");
                option.value = role.name;
                option.text = role.name.replace("ROLE_", "");
                if (selectedRoles.includes(role.name)) {
                    option.selected = true; // Отмечаем роли, которые уже есть у пользователя
                }
                select.appendChild(option);
            });
        });
}

// === ОТПРАВКА ФОРМЫ РЕДАКТИРОВАНИЯ ===
document.getElementById("editUserForm").addEventListener("submit", function (e) {
    e.preventDefault(); // Отключаем стандартную отправку формы

    // Собираем данные с формы
    const id = document.getElementById("editUserId").value;
    const rolesSelect = document.getElementById("editRoles");
    const selectedRoles = Array.from(rolesSelect.selectedOptions).map(opt => ({
        name: opt.value
    }));

    const updatedUser = {
        id: id,
        firstName: document.getElementById("editFirstName").value,
        lastName: document.getElementById("editLastName").value,
        age: document.getElementById("editAge").value,
        email: document.getElementById("editEmail").value,
        roles: selectedRoles
    };

    // Отправляем PUT-запрос с обновлённым пользователем
    fetch(`/api/users/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(updatedUser)
    })
        .then(response => {
            if (response.ok) {
                bootstrap.Modal.getInstance(document.getElementById("editUserModal")).hide(); // Закрываем модалку
                loadUsers(); // Перезагружаем таблицу
            } else {
                alert("Ошибка при сохранении"); // Ошибка обновления
            }
        });
});