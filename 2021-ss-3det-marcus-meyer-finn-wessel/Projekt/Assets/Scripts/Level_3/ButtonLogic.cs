using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class ButtonLogic : MonoBehaviour {
    private bool _pressed = false;
    private AudioSource _audioSource;
    private GameObject _canvas;
    private void Start() {
       _audioSource = gameObject.GetComponent<AudioSource>();
       _canvas = gameObject.transform.GetChild(0).gameObject;
    }

    public void OnPress() {
       _audioSource.Play();
       Debug.Log("pressed");
       _pressed = true;
       _canvas.SetActive(false);
        GameManager.Instance.IncObj();
    }

    private void Update() {
        if (_pressed) {
            transform.localScale = Vector3.Lerp(transform.localScale,
                new Vector3(0.2f, transform.localScale.y, transform.localScale.z), Time.deltaTime * 4);
        }
    }
}
