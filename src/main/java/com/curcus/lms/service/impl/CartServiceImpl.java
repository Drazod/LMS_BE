package com.curcus.lms.service.impl;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.curcus.lms.model.entity.*;
import com.curcus.lms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.curcus.lms.exception.ApplicationException;
import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.exception.ValidationException;
import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.model.mapper.CourseMapper;
import com.curcus.lms.model.response.CartResponse;
import com.curcus.lms.model.response.CourseResponse;
import com.curcus.lms.model.response.CourseResponseForCart;
import com.curcus.lms.service.CartService;
import com.curcus.lms.service.CourseService;
import com.curcus.lms.service.EnrollmentService;
import com.curcus.lms.service.StudentService;
import java.util.Optional;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemsRepository cartItemsRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemsRepository orderItemsRepository;
    @Autowired
    private StudentService StudentService;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private CartItemsRepository cartItemRepository;
    @Autowired
    private StudentService studentService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Cart getCartById(Long studentId) {
        return cartRepository.getCartNotPaidByStudentId(studentId);
    }

    @Override
    public CartItems getById(Long cartId, Long courseId) {
        CartItemsId cartItemsId = new CartItemsId(cartId, courseId);
        return cartItemRepository.findById(cartItemsId).orElse(null);
    }

    @Override
    public Cart createCart(Long studentId) {
        // check valid studentId
        try {
            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) {
                throw new NotFoundException("Student has not existed with id " + studentId);
            }
            Cart cart = getCartById(studentId);
            if (cart == null) {
                cart = new Cart();
                cart.setStudent(student);
                cartRepository.save(cart);
            } else {
                throw new ValidationException("Cart has already created for studentId " + studentId);
            }
            return cart;
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @Override
    public CartItems addCourseToCart(Long studentId, Long courseId) {
        try {
            // check valid courseId
            Course course = courseService.findById(courseId);
            if (course == null) {
                throw new NotFoundException("Course has not existed with id " + courseId);
            }
            // check valid studentId
            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) {
                throw new NotFoundException("Student has not existed with id " + studentId);
            }
            // check enrolled course
            Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);

            if (enrollment != null) {
                throw new ValidationException("Course already enrolled");
            }
            // Check cart is created or not
            Cart cart = getCartById(studentId);
            if (cart == null) {
                cart = new Cart();
                cart.setStudent(student);
                cartRepository.save(cart);
            }
            // check course alreay in cart
            if (getById(cart.getCartId(), courseId) != null) {
                throw new ValidationException("CourseId " + courseId + " already in cart");
            }

            // add course
            // create cartItems and save
            CartItemsId cartItemsId = new CartItemsId(cart.getCartId(), courseId);
            CartItems cartItems = new CartItems();
            cartItems.setId(cartItemsId);
            cartItems.setCart(cart);
            cartItems.setCourse(course);
            cartItemRepository.save(cartItems);
            // Update totalPriceOfCart
            cartRepository.save(cart);
            return cartItems;
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        }
    }

    public CartResponse getById(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        CartResponse cartResponse = new CartResponse(cart.getCartId(), cart.getStudent());

        return cartResponse;
    }

    @Override
    public CartResponse getCartByStudentId(Long studentId) {
        try {
            Cart cart = cartRepository.findCartByStudent_UserId(studentId);
            if (cart == null) {
                throw new NotFoundException("Cart not found");
            }
            CartResponse cartResponse = new CartResponse(cart.getCartId(), cart.getStudent());
            return cartResponse;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Transactional
    public void copyCartToOrder(Long studentId, List<Long> courseIds, Long totalPrice) {
        try {
            Cart cart = cartRepository.findCartByStudent_UserId(studentId);
            if (cart == null) {
                throw new NotFoundException("Cart not found");
            }
            List<CartItems> cartItems = cartItemsRepository.findAllByCart_CartId(cart.getCartId());

            if (cartItems.isEmpty()) {
                throw new NotFoundException("No items in the cart");
            }

            Order order = Order.builder()
                    .student(cart.getStudent())
                    .paymentDate(new Date())
                    .totalPrice(totalPrice)
                    .build();

            Order savedOrder = orderRepository.save(order);

            List<OrderItems> orderItemsList = new ArrayList<>();
            List<CartItems> cartItemsToDelete = new ArrayList<>();

            for (CartItems cartItem : cartItems) {
                if (courseIds.contains(cartItem.getCourse().getCourseId()) &&
                        enrollmentRepository.findByStudent_UserIdAndCourse_CourseId(studentId,
                                cartItem.getCourse().getCourseId()) == null) {

                    OrderItems orderItem = OrderItems.builder()
                            .id(new OrderItemsId(savedOrder.getOrderId(), cartItem.getCourse().getCourseId()))
                            .order(savedOrder)
                            .course(cartItem.getCourse())
                            .price(cartItem.getCourse().getPrice())
                            .build();
                    StudentService.addStudentToCourse(studentId, cartItem.getCourse().getCourseId());
                    orderItemsList.add(orderItem);
                    cartItemsToDelete.add(cartItem);
                }
            }

            orderItemsRepository.saveAll(orderItemsList);

            cartItemsRepository.deleteAll(cartItemsToDelete);
        } catch (Exception ex) {
            throw new RuntimeException("An error occurred while copying the cart to the order", ex);
        }
    }

    @Override
    public Long getIdCartFromStudent(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
            throw new NotFoundException("Student has not existed with id " + studentId);
        }
        Cart cart = cartRepository.findCartByStudent_UserId(studentId);
        if (cart == null) {
            throw new NotFoundException("Cart not found");
        }
        return cart.getCartId();
    }

    @Override
    public void deleteCourseFromCart(Long studentId, Long courseId) {
        try {
            // check valid courseId
            Course course = courseService.findById(courseId);
            if (course == null) {
                throw new NotFoundException("Course has not existed with id " + courseId);
            }
            // check valid studentId
            if (studentService.findById(studentId) == null) {
                throw new NotFoundException("Student has not existed with id " + studentId);
            }

            // Check cart is created or not
            Cart cart = cartRepository.getCartByStudentId(studentId);
            if (cart == null) {
                throw new ValidationException("Cart doesn't exist for studentId: " + studentId);
            }
            // Check if course is in cart
            if (getById(cart.getCartId(), courseId) == null) {
                throw new ValidationException("CourseId " + courseId + " not found in cart");
            }
            // delete cart
            CartItemsId cartItemsId = new CartItemsId(cart.getCartId(), courseId);
            cartItemRepository.deleteById(cartItemsId);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @Override
    public void deleteAllCourseFromCart(Long studentId) {
        try {
            Student student = studentRepository.findById(studentId).orElse(null);

            if (student == null) {
                throw new NotFoundException("Student has not existed with id " + studentId);
            }
            // Check cart is created or not
            Cart cart = cartRepository.getCartByStudentId(studentId);
            if (cart == null) {
                throw new ValidationException("Cart doesn't exist for studentId: " + studentId);
            }
            // delete allCourseInCart
            cartItemRepository.deleteCartItemsById(cart.getCartId());
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    @Override
    public void deleteCart(Long studentId) {
        try {
            // check valid studentId
            Student student = studentRepository.findById(studentId).orElse(null);

            if (student == null) {
                throw new NotFoundException("Student has not existed with id " + studentId);
            }

            Cart cart = cartRepository.getCartByStudentId(studentId);
            if (cart == null) {
                throw new ValidationException("Cart doesn't exist for studentId: " + studentId);
            }
            Long cartId = cart.getCartId();
            cartItemRepository.deleteCartItemsById(cartId);
            cartRepository.deleteById(cartId);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ApplicationException();
        }
    }

    public void deleteListCourseFromCart(Long studentId, List<Long> listCourseDelete) {
        // check valid studentId
        if (studentService.findById(studentId) == null) {
            throw new NotFoundException("Student has not existed with id " + studentId);
        }
        // Check cart is created or not
        Cart cart = cartRepository.getCartByStudentId(studentId);
        if (cart == null) {
            throw new ValidationException("Cart doesn't exist for studentId: " + studentId);
        }
        for (Long courseId : listCourseDelete) {
            Course course = courseService.findById(courseId);

            if (course == null) {
                throw new NotFoundException("Course has not existed with id " + courseId);
            }

            // Check if course is in cart
            if (getById(cart.getCartId(), courseId) == null) {
                throw new ValidationException("CourseId " + courseId + " not found in cart");
            }
            CartItemsId cartItemsId = new CartItemsId(cart.getCartId(), courseId);
            cartItemRepository.deleteById(cartItemsId);

        }

    }
}
