using UnityEngine;


public class PaperTrigger : MonoBehaviour {
    private GameObject _canvas;

    private void Start() {
        _canvas = transform.parent.gameObject.transform.GetChild(1).gameObject;
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
