using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class SuccessUIController : MonoBehaviour {
    private Canvas _successUi;
    private Text _text;
    void Start() {
        _successUi = gameObject.GetComponent<Canvas>();
        _successUi.enabled = false;
        _text = GameObject.Find("ClearTime").GetComponent<Text>();
        GameManager.Instance.SuccessEvent.AddListener(Show);
    }

    public void Show() {
        TimeSpan t = GameManager.Instance.ClearTime[SceneManager.GetActiveScene().buildIndex - 2];
        _text.text = $"Clear Time: {t.Minutes}:{t.Seconds}:{t.Milliseconds}";
        _successUi.enabled = GameManager.Instance.Success;
    }

    public void NextScene() {
        GameManager.Instance.LoadNextScene();
    }
}
