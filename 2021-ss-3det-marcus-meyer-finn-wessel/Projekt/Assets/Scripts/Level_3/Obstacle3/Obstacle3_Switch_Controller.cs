using System.Collections;
using System.Collections.Generic;
using DefaultNamespace;
using UnityEngine;

public class Obstacle3_Switch_Controller : MonoBehaviour, IInteractionLogic
{
    private AudioSource _audioSource;
    private Obstacle_3_Controller _obstacle3Controller;
    private Animator _buttonAnimator;
    public GameObject Obstacle3;
    private void Start()
    {
        _audioSource = gameObject.GetComponent<AudioSource>();
        _obstacle3Controller = Obstacle3.transform.GetComponent<Obstacle_3_Controller>();
        _buttonAnimator = gameObject.GetComponent<Animator>();
    }

    public void Interact() {
        _buttonAnimator.Play("Obstacle3Button");
        Debug.Log(gameObject.transform.parent.name);
        _audioSource.Play();
        _obstacle3Controller.CalculatePath();
        _obstacle3Controller.LightUpPath();
    }
}
