using UnityEngine;

public class ShieldController : MonoBehaviour {
    private Animator _shieldAnimator;
    private MeshRenderer _shieldRenderer;
    private AudioSource _shieldAudio;

    void Start() {
        GameManager.Instance.LifeEvent.AddListener(playAnimation);
        _shieldAnimator = transform.GetComponent<Animator>();
        _shieldRenderer = transform.GetComponent<MeshRenderer>();
        _shieldAudio = transform.GetComponent<AudioSource>();

        if (GameManager.Instance.ShieldCounter > 0) {
            _shieldRenderer.enabled = true;
        }
    }

    private void playAnimation() {
        if (GameManager.Instance.ShieldCounter >= 0.5f && !_shieldRenderer.enabled) {
            _shieldRenderer.enabled = true;
            _shieldAnimator.Play("shield_load");
            _shieldAudio.Play();
        }
        else if (GameManager.Instance.LifeCounter < 1f && GameManager.Instance.ShieldCounter <= 0f) {
            _shieldRenderer.enabled = false; 
        }
    }
}
