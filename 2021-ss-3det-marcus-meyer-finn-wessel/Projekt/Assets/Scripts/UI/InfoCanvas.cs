using UnityEngine;

public class InfoCanvas : MonoBehaviour {
    private Transform _camera;
    private void Start() {
        _camera = GameObject.Find("Cameras").GetComponent<Transform>();
    }
    
    private void Update() {
        transform.LookAt(_camera);
    }
}
