using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class FailureUIController : MonoBehaviour {
    private Canvas _failureUi;
    private Text _reasonText;
    
    void Start() {
        _failureUi = gameObject.GetComponent<Canvas>();
        _reasonText = transform.Find("Title").GetComponent<Text>();
        _failureUi.enabled = false;
        GameManager.Instance.FailureEvent.AddListener(Show);
    }

    public void Show() {
        _reasonText.text = GameManager.Instance.FailureReason;
        _failureUi.enabled = GameManager.Instance.Failure;
    }

    public void Restart() {
        GameManager.Instance.ReloadScene();
    }
}
