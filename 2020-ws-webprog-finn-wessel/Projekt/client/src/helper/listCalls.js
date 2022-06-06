export function getListsFromUser(jwt) {
  return fetch("http://localhost:3000/list", {
    headers: new Headers({
      Authorization: "Bearer " + jwt
    })
  }).then(response => {
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    return response.json();
  });
}

export function getListWithId(id, jwt) {
  return fetch(`http://localhost:3000/list/${id}`, {
    headers: new Headers({
      Authorization: "Bearer " + jwt
    })
  }).then(response => {
    if (!response.ok) {
      throw {
        status: response.status,
        message: "Network response was not ok"
      };
    }
    return response.json();
  });
}

export function createList(data, jwt) {
  return fetch("http://localhost:3000/list", {
    method: "POST",
    headers: new Headers({
      Authorization: "Bearer " + jwt,
      "Content-Type": "application/json"
    }),
    body: JSON.stringify(data)
  }).then(response => {
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
  });
}

export function updateList(data, jwt) {
  return fetch(`http://localhost:3000/list/${data.id}`, {
    method: "PATCH",
    headers: new Headers({
      Authorization: "Bearer " + jwt,
      "Content-Type": "application/json"
    }),
    body: JSON.stringify(data)
  }).then(response => {
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
    return response.json();
  });
}

export function deleteList(id, jwt) {
  return fetch(`http://localhost:3000/list/${id}`, {
    method: "DELETE",
    headers: new Headers({
      Authorization: "Bearer " + jwt
    })
  }).then(response => {
    if (!response.ok) {
      throw new Error("Network response was not ok");
    }
  });
}
