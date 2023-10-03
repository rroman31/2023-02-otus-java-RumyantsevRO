async function loadClients() {
    let resp = await fetch("http://localhost:8080/clients")
    return resp.json();
}

async function createTable() {

    let clients = await loadClients();

    let container = document.getElementById("container");

    let table = document.createElement("table");

    let cols = Object.keys(clients[0]);

    let thead = document.createElement("thead");
    let tr = document.createElement("tr");

    let radioButtonTh = document.createElement("th");
    tr.appendChild(radioButtonTh)


    cols.forEach((item) => {
        let th = document.createElement("th");
        th.innerText = item;
        tr.appendChild(th);
    });
    thead.appendChild(tr);
    table.append(tr);

    clients.forEach((item) => {
        let tr = document.createElement("tr");
        let td = document.createElement("td");
        td.innerHTML = "<input type=\"radio\" id=\"" + item.id + "\" name=\"clients_table\" />";
        tr.appendChild(td);

        let td_id = document.createElement("td");
        td_id.innerText = item.id;
        tr.appendChild(td_id);

        let td_name = document.createElement("td");
        td_name.innerText = item.name;
        tr.appendChild(td_name);

        let td_address = document.createElement("td");
        td_address.innerText = item.address.street;
        tr.appendChild(td_address);

        let td_phones = document.createElement("td");
        let phones = item.phone.map(val => val.number).map(value => ' ' + value + '').join()
        td_phones.innerText = phones;
        tr.appendChild(td_phones);
        table.appendChild(tr);
    });
    container.appendChild(table);
}

async function addClient() {

    if (document.getElementById('creation_cli_phone').value == "" || document.getElementById('creation_cli_street').value == "" || document.getElementById('creation_cli_name').value == "") {
        alert("All fields must be filled out");
        return false;
    }

    let phone = {
        number: document.getElementById('creation_cli_phone').value
    }

    let address = {
        street: document.getElementById('creation_cli_street').value
    }

    let client = {
        id: null, name: document.getElementById('creation_cli_name').value, address: address, phone: [phone]
    };

    const response = await fetch('http://localhost:8080/clients', {
        method: 'POST', headers: {
            'Content-Type': 'application/json; charset=utf-8'
        }, body: JSON.stringify(client),
    })

    location.reload();
}


async function updateClient() {

    if (document.getElementById('update_cli_phone').value == "" || document.getElementById('update_cli_street').value == "" || document.getElementById('update_cli_name').value == "") {
        alert("All fields must be filled out");
        return false;
    }

    let phone = {
        number: document.getElementById('update_cli_phone').value
    }

    let address = {
        street: document.getElementById('update_cli_street').value
    }

    let client = {
        id: null, name: document.getElementById('update_cli_name').value, address: address, phone: [phone]
    };

    let clientId = document.getElementById("update_cli_id").value

    console.log("clientId")
    console.log(clientId)

    const response = await fetch(`http://localhost:8080/clients/${clientId}`, {
        method: 'PUT', headers: {
            'Content-Type': 'application/json; charset=utf-8'
        }, body: JSON.stringify(client),
    })
    location.reload();
}

function findCheckBox() {
    let id;
    document.querySelectorAll('[name="clients_table"]').forEach(el => {
        if (el.checked) {
            console.log(`found ${el.id}`)
            id = el.id;
            return id;

        }
    });
    return id;
}

async function removeClient() {
    let id = findCheckBox();
    if (id == null) alert("Select radiobutton");
    const response = await fetch(`http://localhost:8080/clients/${id}`, {
        method: 'DELETE', headers: {
            'Content-Type': 'application/json; charset=utf-8'
        }, body: null,
    })
    location.reload();
}
