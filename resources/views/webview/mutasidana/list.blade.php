@inject('helper', 'App\Helpers\HelpersFunction')
@extends('webview.layout')
@section('content')
<!-- Content Header (Page header) -->
<section class="content" style="padding-top: 10px;">
   <div class="container-fluid">
      <div class="row">
         <div class="col-12">
            <div class="alert alert-warning" role="alert">
                Dana yang anda miliki sampai {!! date('d-M-Y H:i:s') !!} sebesar: {{ $helper->rupiah($dana_tersisa) }}
            </div>
            <div class="card">
               <div class="card-header container-fluid">
                  <div class="row">
                    
                     <div class="col-md-12">
                        <a href="{{ URL::to('/webview/permohonan-penarikan-dana/' . $user_id) }}" class="btn btn-primary float-right btn-block">Ajukan Penarikan</a>              
                     </div>
                  </div>
               </div>
               <!-- /.card-header -->
               <div class="card-body">
                  <table id="dataTables" class="table table-bordered table-striped">
                     <thead>
                        <tr>
                           <th>Tanggal</th>                           
                           <th>Nominal</th>
                          
                           <th>Keterangan</th>  
                           <th>Status</th>                           
                           
                        </tr>
                     </thead>
                     <tbody>
                        @foreach($data as $d)
                        <tr>
                           <td>{{ substr($d->created_at,0,10) }}</td>
                           <td>{!! $helper->rupiah($d->nominal) !!}</td>
                          
                           <td>{!! $d->keterangan !!}</td>
                           <td>
                           @if($d->status == 'PND')
                           <span class="badge badge-warning">MUTASI:ON-PROCESS</span>
                           @elseif($d->status == 'IN')
                           <span class="badge badge-success">MUTASI:IN</span>
                           @else
                           <span class="badge badge-danger">MUTASI:OUT</span>
                           @endif 
                           </td>
                        </tr>
                        @endforeach
                     </tbody>
                  </table>
               </div>
               <!-- /.card-body -->
            </div>
            <!-- /.card -->
         </div>
      </div>
   </div>
</section>
<script>
   $(function () {
     
     $('#dataTables').DataTable({
       "paging": true,
       "lengthChange": false,
       "searching": true,
       "ordering": true,
       "info": true,
       "autoWidth": false,
       "responsive": true,
     });
   });
</script>
@endsection