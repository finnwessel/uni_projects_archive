using System.Collections;
using System.Collections.Generic;
using DefaultNamespace;
using UnityEngine;
using UnityEngine.InputSystem;

public class PaperLogic : MonoBehaviour, IInteractionLogic {
    private MeshRenderer _meshRenderer;
    private AudioSource _audioSource;
    private GameObject _canvas;
    private void Start() {
        _audioSource = gameObject.GetComponent<AudioSource>();
        _meshRenderer = GetComponent<MeshRenderer>();
        _canvas = gameObject.transform.GetChild(1).gameObject;
    }

    public void Interact() {
        _audioSource.PlayDelayed(0);
        _meshRenderer.enabled = false;
        _canvas.SetActive(false);
        GameManager.Instance.IncObj();
        Destroy(gameObject, 1);
    }
}
