using DefaultNamespace;
using UnityEngine;

public class SwitchController : MonoBehaviour, IInteractionLogic {
    private Material _pulseLightMaterial;
    private bool _buttonActivated = false;
    private AudioSource _audioSource;
    private void Start() {
        _pulseLightMaterial = gameObject.transform.parent.Find("Lamp/Bulb").GetComponent<MeshRenderer>().materials[0];
        _audioSource = gameObject.GetComponent<AudioSource>();
    }

    public void Interact() {
        if (!_buttonActivated) {
            _pulseLightMaterial.SetColor("PulseLightColor", Color.green);
            _audioSource.Play();
            _buttonActivated = true;
            LevelManager.Instance.SwitchPressed();
        }
    }

    private void Update() {
        if (_buttonActivated) {
            transform.localScale = Vector3.Lerp(transform.localScale,
                new Vector3(0.2f, transform.localScale.y, transform.localScale.z), Time.deltaTime * 4);
        }
    }
    
}
