using System.Collections;
using System.Collections.Generic;
using DefaultNamespace;
using UnityEngine;

public class Terminal_Controller : MonoBehaviour, IInteractionLogic {
    private Canvas _canvas;
    void Start() {
        _canvas = transform.Find("InfoCanvas").GetComponent<Canvas>();
    }

    public void Interact() {
        _canvas.enabled = false;
        GameManager.Instance.Won();
    }
}
