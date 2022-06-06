export function loginUser(data, jwt){
  return fetch("http://localhost:3000/auth/login", {
    method: "POST",
    headers: new Headers({
      Authorization: "Bearer " + jwt,
      "Content-Type": "application/json"
    }),
    body: JSON.stringify(data)
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

export function logoutUser(refresh_token){
  return fetch("http://localhost:3000/auth/logout", {
    method: "POST",
    headers: new Headers({
      "Content-Type": "application/json"
    }),
    body: JSON.stringify({
      refresh_token
    })
  }).then(response => {
    if (!response.ok) {
      throw {
        status: response.status,
        message: "Network response was not ok"
      };
    }
  });
}

export function registerUser(data) {
  return fetch("http://localhost:3000/auth/register", {
    method: "POST",
    headers: new Headers({
      "Content-Type": "application/json"
    }),
    body: JSON.stringify(data)
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

export function refreshAccessToken(data, jwt){
  return fetch("http://localhost:3000/auth/refresh", {
    method: "POST",
    headers: new Headers({
      Authorization: "Bearer " + jwt,
      "Content-Type": "application/json"
    }),
    body: JSON.stringify(data)
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
