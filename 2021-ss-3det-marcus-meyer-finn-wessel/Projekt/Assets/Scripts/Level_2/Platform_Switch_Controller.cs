using UnityEngine;

public class Platform_Switch_Controller : MonoBehaviour {
    private Objective_Controller _obj;
    private bool _isTriggered;
    public bool IsTriggered => _isTriggered;
    private Rigidbody _rb;
    private Material _material;
    private void Start() {
        _obj = transform.parent.parent.GetComponent<Objective_Controller>();
        _material = transform.parent.GetComponent<MeshRenderer>().material;
    }

    private void OnTriggerEnter(Collider other) {
        if (other.gameObject.name == "Key_Sphere") {
            other.transform.position = transform.position;
                other.gameObject.GetComponent<Rigidbody>().velocity = Vector3.zero;
                _material.EnableKeyword("_EMISSION");
                _isTriggered = true;
                _obj.Unlock();
        }
    }

    private void OnTriggerExit(Collider other) {
        if (other.gameObject.name == "Key_Sphere") {
            _isTriggered = false;
            _material.DisableKeyword("_EMISSION");
        }
    }
}
