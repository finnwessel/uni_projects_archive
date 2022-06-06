using UnityEngine;
using UnityEngine.UI;

public class GateTrigger : MonoBehaviour {
    private GameObject _canvas;
    private Text _text;

    private void Start() {
        _canvas = transform.parent.Find("InfoCanvas").gameObject;
        _canvas.SetActive(false);
        _text = transform.parent.Find("InfoCanvas/Text").GetComponent<Text>();
    }
    
    private void OnTriggerEnter(Collider other) {
        if (other.CompareTag("Player")) {
            _canvas.SetActive(true);
        }
    }
    
    private void OnTriggerExit(Collider other) {
        _canvas.SetActive(false);
        _text.text = "Enter Code";
    }
}
