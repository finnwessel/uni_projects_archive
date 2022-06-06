using System;
using System.Collections;
using DefaultNamespace;
using UnityEngine;
using UnityEngine.InputSystem;


public class PlayerController : MonoBehaviour {
    private Rigidbody _player;
    private CharacterController controller;
    private Transform _camTransform;

    // holds the currently triggered object. Holds an empty object when not colliding.
    private GameObject _collider;
    private GameObject _emptyCollider;

    private Vector2 _movementVector;
    private RaycastHit _groundHit;
    [Header("Settings")]
    public float accelerationSpeed;
    public float maxSpeed;
    private float _maxSpeedTemp;
    public float jumpSpeed;
    public float JumpHeight;
    [Header("Debugging")] 
    public bool groundSphereCast;

    private void Start() {
        _emptyCollider = new GameObject {
            name = "EmptyCollider",
            tag = "Empty"
        };
        ResetCollider();
        _player = GameObject.Find("Player").GetComponent<Rigidbody>();
        _camTransform = GameObject.Find("Cameras").GetComponent<Transform>();
        _maxSpeedTemp = maxSpeed;
    }

    private bool playerIsOnGround() {
        return _groundHit.distance <= _player.transform.localScale.y / 1.9;
    }
    public void OnMove(InputAction.CallbackContext context) {
        _movementVector = context.ReadValue<Vector2>();
    }

    public void OnJump(InputAction.CallbackContext context) {
        if (context.performed) {
            if (playerIsOnGround() && _groundHit.collider.tag != "NonJumpable" && _groundHit.collider.name != "Trigger") {
                Vector3 movement = new Vector3(0f, JumpHeight, 0f);
                _player.AddForce(movement * jumpSpeed);
            }
        }
    }

    public void OnInteraction(InputAction.CallbackContext context) {
        if (context.performed) {
            if (!_collider.CompareTag("Empty")) {
                _collider.GetComponent<IInteractionLogic>().Interact();
                ResetCollider();
            }
        }
    }

    private void OnTriggerEnter(Collider other) {
        switch (other.gameObject.tag) {
            case "Interactable":
                _collider = other.transform.parent.gameObject;
                break;
            case "Bullet":
                GameManager.Instance.DecLife(0.1f);
                Destroy(other.gameObject);
                break;
            case "Finish":
                GameManager.Instance.Won();
                break;
            case "Water":
                maxSpeed = 8f;
                break;
        }
    }

    private void OnTriggerStay(Collider other) {
        switch (other.gameObject.tag) {
            case "Water":
                GameManager.Instance.DecLife(0.1f * Time.deltaTime);
                break;
        }
    }

    private void OnTriggerExit(Collider other) {
        switch (other.gameObject.tag) {
            case "Water":
                maxSpeed = _maxSpeedTemp;
                break;
        }
        ResetCollider();
    }

    private void ResetCollider() {
        _collider = _emptyCollider;
    }

    void Update() {
        // player movement
        Vector3 movingDirection = _camTransform.TransformDirection(new Vector3(_movementVector.x, 0.0f, _movementVector.y));
        movingDirection.y = 0;
        _player.AddForce(movingDirection * (accelerationSpeed * Time.deltaTime), ForceMode.Impulse);
        
        // limit speed
        _player.velocity = new Vector3(Mathf.Clamp(_player.velocity.x, -maxSpeed, maxSpeed), _player.velocity.y, Mathf.Clamp(_player.velocity.z, -maxSpeed, maxSpeed));

        // Ground detection for jumping
        float radius = _player.transform.lossyScale.y / 2;
        Vector3 origin = new Vector3(_player.position.x, transform.position.y + radius, transform.position.z);
        Physics.SphereCast(origin, radius, Vector3.down, out _groundHit);
    }

    private void OnDrawGizmos() {
        // debug ground spherecast
        if (groundSphereCast) {
            Vector3 position = transform.position;
            float radius = transform.lossyScale.y / 2;
            Vector3 origin = new Vector3(position.x, position.y + radius, position.z);
            Gizmos.color = _groundHit.distance <= transform.lossyScale.y / 1.9 ? Color.black : Color.red;
            Gizmos.DrawLine(origin, _groundHit.point);
            Gizmos.DrawWireSphere(origin + Vector3.down * _groundHit.distance, radius);
        }
    }
}
