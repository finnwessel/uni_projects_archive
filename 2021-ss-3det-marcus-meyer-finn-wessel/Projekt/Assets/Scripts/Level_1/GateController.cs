using DefaultNamespace;
using UnityEngine;
using UnityEngine.UI;

public class GateController : MonoBehaviour, IInteractionLogic {
    private Animator _animator;
    private Canvas _canvas;
    private Text _text;
    private AudioSource _mainAudio;
    private AudioClip _endMusic;
    void Start() {
        _animator = GetComponent<Animator>();
        _canvas = transform.Find("InfoCanvas").GetComponent<Canvas>();
        _text = transform.Find("InfoCanvas/Text").GetComponent<Text>();
        _mainAudio = GameObject.Find("Main Camera").GetComponent<AudioSource>();
        _endMusic = Resources.Load<AudioClip>("Audio/Level1_Part3");
    }

    public void Interact() {
        if (GameManager.Instance.ObjectiveCounter >= 4) {
            _animator.SetTrigger("open");
            _canvas.enabled = false;
            _mainAudio.clip = _endMusic;
            _mainAudio.Play();
        } else {
            _text.text = GameManager.Instance.ObjectiveCounter + "/4 Digits Found";
        }
    }
    
}
