using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Light_Controller : MonoBehaviour {
    private GameObject _player;
    private float _offset;
    private void Start() {
        _player = GameObject.Find("Player");
        _offset = transform.position.y - _player.transform.position.y;
    }

    void Update() {
        transform.position = new Vector3(_player.transform.position.x, _player.transform.position.y + _offset, _player.transform.position.z);
    }
}
