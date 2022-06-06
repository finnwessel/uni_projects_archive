using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Objective_Controller : MonoBehaviour {
    private HudController _hud;
    private AudioSource[] _switcheAudio = new AudioSource[3];
    private Platform_Switch_Controller[] _switches= new Platform_Switch_Controller[3];
    private Animator[] _elevatorPlatform = new Animator[2];
    void Start() {
        _hud = GameObject.Find("HUD").GetComponent<HudController>();
        for (int i = 0; i < _switches.Length; i++) {
            _switcheAudio[i] = gameObject.transform.GetChild(i).gameObject.GetComponent<AudioSource>();
            _switches[i] = gameObject.transform.GetChild(i).GetChild(0)
                .GetComponent<Platform_Switch_Controller>();
        }

        _elevatorPlatform[0] = GameObject.Find("Elevator/Platform_1").GetComponent<Animator>();
        _elevatorPlatform[1] = GameObject.Find("Elevator/Platform_2").GetComponent<Animator>();
    }

    public void Unlock() {
        if (_switches[1].IsTriggered && _switches[2].IsTriggered) {
            _hud.ShowToast("3rd floor unlocked", 4);
            _switcheAudio[1].Play();
            _elevatorPlatform[0].Play("elevator_1_3");
        } else if (GameManager.Instance.ObjectiveCounter <= 0) {
            _hud.ShowToast("2nd floor unlocked", 4);
            _switcheAudio[0].Play();
            _elevatorPlatform[0].Play("elevator_1_2");
            GameManager.Instance.IncObj();
        }
    }
}
