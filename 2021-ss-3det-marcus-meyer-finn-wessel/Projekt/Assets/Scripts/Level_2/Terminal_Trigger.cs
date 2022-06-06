using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Terminal_Trigger : MonoBehaviour {
    private GameObject _canvas;

    private void Start() {
        _canvas = transform.parent.Find("InfoCanvas").gameObject;
        _canvas.SetActive(false);
    }
    
    private void OnTriggerEnter(Collider other) {
        if (other.CompareTag("Player")) {
            _canvas.SetActive(true);
        }
    }
    
    private void OnTriggerExit(Collider other) {
        _canvas.SetActive(false);
    }
}
