package com.quyen.shoplite.service;

import com.quyen.shoplite.domain.Order;
import com.quyen.shoplite.domain.Transaction;
import com.quyen.shoplite.domain.request.ReqTransactionDTO;
import com.quyen.shoplite.domain.response.ResTransactionDTO;
import com.quyen.shoplite.repository.OrderRepository;
import com.quyen.shoplite.repository.TransactionRepository;
import com.quyen.shoplite.util.DTOMapper;
import com.quyen.shoplite.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;

    public ResTransactionDTO create(ReqTransactionDTO req) {
        Order order = null;
        if (req.getOrderId() != null) {
            order = orderRepository.findById(req.getOrderId())
                    .orElseThrow(() -> new IdInvalidException("Không tìm thấy Order id=" + req.getOrderId()));
        }
        Transaction transaction = Transaction.builder()
                .amount(req.getAmount())
                .type(req.getType())
                .content(req.getContent())
                .transactionTime(req.getTransactionTime() != null ? req.getTransactionTime() : LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .order(order)
                .build();
        return DTOMapper.toResTransactionDTO(transactionRepository.save(transaction));
    }

    public ResTransactionDTO findById(Integer id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Transaction id=" + id));
        return DTOMapper.toResTransactionDTO(transaction);
    }

    public List<ResTransactionDTO> findAll() {
        return transactionRepository.findAll().stream()
                .map(DTOMapper::toResTransactionDTO)
                .toList();
    }

    public List<ResTransactionDTO> findByOrderId(Integer orderId) {
        return transactionRepository.findAllByOrder_Id(orderId).stream()
                .map(DTOMapper::toResTransactionDTO)
                .toList();
    }
}
